package com.example.seall.util

import com.example.seall.data.model.Order
import com.example.seall.data.model.StockIngredient
import com.example.seall.data.model.StockItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CsvHelper {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

    // ─── Orders ──────────────────────────────────────────────────────────────

    /**
     * Generates a CSV formatted string from a list of Orders.
     */
    fun generateOrdersCsv(orders: List<Order>): String {
        val sb = StringBuilder()
        sb.append("ID,CustomerName,ItemsOrdered,TotalItemCount,Price,IsPaid,Date,CreatedAt\n")
        for (order in orders) {
            val escapedName = escapeCsv(order.customerName)
            val escapedItems = escapeCsv(order.itemsSummary)
            val priceStr = String.format(Locale.US, "%.2f", order.price)
            val isPaidStr = if (order.isPaid) "PAID" else "UNPAID"
            val dateStr = dateFormat.format(Date(order.createdAt))
            sb.append("${order.id},$escapedName,$escapedItems,${order.totalItemCount},$priceStr,$isPaidStr,$dateStr,${order.createdAt}\n")
        }
        return sb.toString()
    }

    /**
     * Parses orders from CSV content. Skips invalid rows gracefully.
     */
    fun parseOrdersCsv(csvContent: String): List<Order> {
        val lines = csvContent.lines().filter { it.isNotBlank() }
        if (lines.isEmpty()) return emptyList()

        val headers = parseCsvLine(lines.first()).map { it.trim().lowercase(Locale.US) }
        val nameIdx    = headers.indexOfFirst { it.contains("customer") || it.contains("name") }
        val itemsIdx   = headers.indexOfFirst { it.contains("itemsordered") || it.contains("items") || it.contains("item_list") }
        val countIdx   = headers.indexOfFirst { it.contains("totalitemcount") || it.contains("itemcount") || it.contains("quantity") || it.contains("count") }
        val priceIdx   = headers.indexOfFirst { it.contains("price") || it.contains("amount") }
        val paidIdx    = headers.indexOfFirst { it.contains("paid") || it.contains("status") }
        val createdIdx = headers.indexOfFirst { it.contains("createdat") || it.contains("timestamp") }
        val dateIdx    = headers.indexOfFirst { it == "date" }

        val orders = mutableListOf<Order>()
        for (i in 1 until lines.size) {
            val tokens = parseCsvLine(lines[i])
            if (tokens.isEmpty()) continue
            try {
                val name = if (nameIdx in tokens.indices) tokens[nameIdx].trim() else "Customer"
                val itemsSummary = if (itemsIdx in tokens.indices) tokens[itemsIdx].trim() else ""
                val totalItemCount = if (countIdx in tokens.indices) tokens[countIdx].trim().toIntOrNull() ?: 1 else 1
                val priceToken = if (priceIdx in tokens.indices) tokens[priceIdx].trim().replace("₱", "").replace(",", "") else "0.0"
                val price = priceToken.toDoubleOrNull() ?: continue
                if (price <= 0.0) continue
                val isPaid = if (paidIdx in tokens.indices) {
                    val p = tokens[paidIdx].trim().lowercase(Locale.US)
                    p == "true" || p == "paid" || p == "yes" || p == "1"
                } else false
                val createdAt = when {
                    createdIdx in tokens.indices && tokens[createdIdx].trim().toLongOrNull() != null ->
                        tokens[createdIdx].trim().toLong()
                    dateIdx in tokens.indices ->
                        try { dateFormat.parse(tokens[dateIdx].trim())?.time ?: System.currentTimeMillis() }
                        catch (e: Exception) { System.currentTimeMillis() }
                    else -> System.currentTimeMillis()
                }
                orders.add(Order(id = 0, customerName = name.ifBlank { "Customer" }, itemsSummary = itemsSummary,
                    totalItemCount = totalItemCount, price = price, isPaid = isPaid, createdAt = createdAt))
            } catch (_: Exception) { }
        }
        return orders
    }

    // ─── Stocks ───────────────────────────────────────────────────────────────

    fun generateStocksCsv(stocks: List<StockItem>): String {
        val sb = StringBuilder()
        sb.append("ID,Name,Price,Quantity,CreatedAt\n")
        for (s in stocks) {
            sb.append("${s.id},${escapeCsv(s.name)},${String.format(Locale.US, "%.2f", s.price)},${s.quantity},${s.createdAt}\n")
        }
        return sb.toString()
    }

    fun parseStocksCsv(csvContent: String): List<StockItem> {
        val lines = csvContent.lines().filter { it.isNotBlank() }
        if (lines.isEmpty()) return emptyList()
        val headers = parseCsvLine(lines.first()).map { it.trim().lowercase(Locale.US) }
        val nameIdx     = headers.indexOfFirst { it == "name" }
        val priceIdx    = headers.indexOfFirst { it == "price" }
        val quantityIdx = headers.indexOfFirst { it == "quantity" }
        val createdIdx  = headers.indexOfFirst { it == "createdat" }

        val stocks = mutableListOf<StockItem>()
        for (i in 1 until lines.size) {
            val tokens = parseCsvLine(lines[i])
            if (tokens.isEmpty()) continue
            try {
                val name = if (nameIdx in tokens.indices) tokens[nameIdx].trim() else continue
                if (name.isBlank()) continue
                val price = if (priceIdx in tokens.indices) tokens[priceIdx].trim().replace("₱","").replace(",","").toDoubleOrNull() ?: continue else continue
                if (price <= 0.0) continue
                val quantity = if (quantityIdx in tokens.indices) tokens[quantityIdx].trim().toIntOrNull() ?: 0 else 0
                val createdAt = if (createdIdx in tokens.indices) tokens[createdIdx].trim().toLongOrNull() ?: System.currentTimeMillis() else System.currentTimeMillis()
                stocks.add(StockItem(id = 0, name = name, price = price, quantity = quantity, createdAt = createdAt))
            } catch (_: Exception) { }
        }
        return stocks
    }

    // ─── Stock Ingredients ────────────────────────────────────────────────────

    /**
     * Exports ingredients with [stockName] column so the user can read it easily in a spreadsheet.
     * On import, [stockId] is matched by name lookup.
     */
    fun generateStockIngredientsCsv(ingredients: List<StockIngredient>, stocks: List<StockItem>): String {
        val stockNameMap = stocks.associate { it.id to it.name }
        val sb = StringBuilder()
        sb.append("ID,StockId,StockName,IngredientName,Quantity,Cost\n")
        for (ing in ingredients) {
            val stockName = escapeCsv(stockNameMap[ing.stockId] ?: "")
            sb.append("${ing.id},${ing.stockId},$stockName,${escapeCsv(ing.name)},${escapeCsv(ing.quantity)},${String.format(Locale.US,"%.2f",ing.cost)}\n")
        }
        return sb.toString()
    }

    /**
     * Parses stock ingredients from CSV. Requires a name→id lookup map built from already-imported stocks.
     * Rows with unknown stock names are skipped.
     */
    fun parseStockIngredientsCsv(csvContent: String, stockNameToId: Map<String, Long>): List<StockIngredient> {
        val lines = csvContent.lines().filter { it.isNotBlank() }
        if (lines.isEmpty()) return emptyList()
        val headers = parseCsvLine(lines.first()).map { it.trim().lowercase(Locale.US) }
        val stockIdIdx   = headers.indexOfFirst { it == "stockid" }
        val stockNameIdx = headers.indexOfFirst { it == "stockname" }
        val nameIdx      = headers.indexOfFirst { it == "ingredientname" || it == "name" }
        val quantityIdx  = headers.indexOfFirst { it == "quantity" }
        val costIdx      = headers.indexOfFirst { it == "cost" }

        val result = mutableListOf<StockIngredient>()
        for (i in 1 until lines.size) {
            val tokens = parseCsvLine(lines[i])
            if (tokens.isEmpty()) continue
            try {
                // Resolve stockId by name first (reliable after re-import), then raw ID column
                val stockName = if (stockNameIdx in tokens.indices) tokens[stockNameIdx].trim() else ""
                val rawStockId = if (stockIdIdx in tokens.indices) tokens[stockIdIdx].trim().toLongOrNull() else null
                val resolvedStockId: Long = stockNameToId[stockName]
                    ?: rawStockId
                    ?: continue
                val ingName = if (nameIdx in tokens.indices) tokens[nameIdx].trim() else continue
                if (ingName.isBlank()) continue
                val quantity = if (quantityIdx in tokens.indices) tokens[quantityIdx].trim().ifBlank { "1" } else "1"
                val cost = if (costIdx in tokens.indices) tokens[costIdx].trim().replace("₱","").replace(",","").toDoubleOrNull() ?: 0.0 else 0.0
                result.add(StockIngredient(id = 0, stockId = resolvedStockId, name = ingName, quantity = quantity, cost = cost))
            } catch (_: Exception) { }
        }
        return result
    }

    // ─── Full Backup ZIP ──────────────────────────────────────────────────────

    /**
     * Produces a combined backup string with clearly delimited sections.
     * Format: three CSV blocks separated by sentinel lines.
     */
    fun generateFullBackup(orders: List<Order>, stocks: List<StockItem>, ingredients: List<StockIngredient>): String {
        return buildString {
            append("### SEALL_BACKUP_V1 ###\n")
            append("### SECTION: ORDERS ###\n")
            append(generateOrdersCsv(orders))
            append("### SECTION: STOCKS ###\n")
            append(generateStocksCsv(stocks))
            append("### SECTION: STOCK_INGREDIENTS ###\n")
            append(generateStockIngredientsCsv(ingredients, stocks))
            append("### END ###\n")
        }
    }

    data class FullBackup(
        val orders: List<Order>,
        val stocks: List<StockItem>,
        val stockIngredients: List<StockIngredient>
    )

    fun parseFullBackup(content: String): FullBackup {
        // Split on section sentinels
        val sections = content.split(Regex("### SECTION: (ORDERS|STOCKS|STOCK_INGREDIENTS) ###\\n?"))
        // sections[0] = header, [1] = orders csv, [2] = stocks csv, [3] = ingredients csv (possibly trailing ### END ###)
        fun extractSection(raw: String) = raw.substringBefore("### ").trim()

        val ordersCsv = if (sections.size > 1) extractSection(sections[1]) else ""
        val stocksCsv = if (sections.size > 2) extractSection(sections[2]) else ""
        val ingCsv    = if (sections.size > 3) extractSection(sections[3]) else ""

        val orders = if (ordersCsv.isNotBlank()) parseOrdersCsv(ordersCsv) else emptyList()
        val stocks = if (stocksCsv.isNotBlank()) parseStocksCsv(stocksCsv) else emptyList()

        // Build name→id map from freshly-parsed stocks (id=0 at this point, so we map by position)
        // We pass an empty map and fall back to index-based resolution when restoring
        val ingredients = if (ingCsv.isNotBlank()) {
            // Build a temporary name→position map; actual DB ids come after insert
            parseStockIngredientsCsv(ingCsv, emptyMap())
        } else emptyList()

        return FullBackup(orders, stocks, ingredients)
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private fun escapeCsv(value: String): String =
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"" + value.replace("\"", "\"\"") + "\""
        } else value

    private fun parseCsvLine(line: String): List<String> {
        val tokens = mutableListOf<String>()
        val sb = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < line.length) {
            val c = line[i]
            when {
                c == '"' -> {
                    if (inQuotes && i + 1 < line.length && line[i + 1] == '"') {
                        sb.append('"'); i++
                    } else inQuotes = !inQuotes
                }
                c == ',' && !inQuotes -> { tokens.add(sb.toString()); sb.clear() }
                else -> sb.append(c)
            }
            i++
        }
        tokens.add(sb.toString())
        return tokens
    }
}
