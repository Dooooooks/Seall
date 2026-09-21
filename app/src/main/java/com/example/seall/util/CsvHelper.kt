package com.example.seall.util

import com.example.seall.data.model.Order
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CsvHelper {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

    /**
     * Generates a CSV formatted string from a list of Orders.
     */
    fun generateOrdersCsv(orders: List<Order>): String {
        val sb = StringBuilder()
        // Header
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
     * Parses orders from CSV content.
     * Skips invalid rows gracefully.
     */
    fun parseOrdersCsv(csvContent: String): List<Order> {
        val lines = csvContent.lines().filter { it.isNotBlank() }
        if (lines.isEmpty()) return emptyList()

        val headerLine = lines.first()
        val headers = parseCsvLine(headerLine).map { it.trim().lowercase(Locale.US) }

        val nameIdx = headers.indexOfFirst { it.contains("customer") || it.contains("name") }
        val itemsIdx = headers.indexOfFirst { it.contains("itemsordered") || it.contains("items") || it.contains("item_list") }
        val countIdx = headers.indexOfFirst { it.contains("totalitemcount") || it.contains("itemcount") || it.contains("quantity") || it.contains("count") }
        val priceIdx = headers.indexOfFirst { it.contains("price") || it.contains("amount") }
        val paidIdx = headers.indexOfFirst { it.contains("paid") || it.contains("status") }
        val createdIdx = headers.indexOfFirst { it.contains("createdat") || it.contains("timestamp") }
        val dateIdx = headers.indexOfFirst { it == "date" }

        val orders = mutableListOf<Order>()

        for (i in 1 until lines.size) {
            val line = lines[i]
            val tokens = parseCsvLine(line)
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
                    createdIdx in tokens.indices && tokens[createdIdx].trim().toLongOrNull() != null -> {
                        tokens[createdIdx].trim().toLong()
                    }
                    dateIdx in tokens.indices -> {
                        try {
                            dateFormat.parse(tokens[dateIdx].trim())?.time ?: System.currentTimeMillis()
                        } catch (e: Exception) {
                            System.currentTimeMillis()
                        }
                    }
                    else -> System.currentTimeMillis()
                }

                orders.add(
                    Order(
                        id = 0, // Auto-generate new primary key to prevent collision
                        customerName = name.ifBlank { "Customer" },
                        itemsSummary = itemsSummary,
                        totalItemCount = totalItemCount,
                        price = price,
                        isPaid = isPaid,
                        createdAt = createdAt
                    )
                )
            } catch (e: Exception) {
                // Ignore corrupt row
            }
        }

        return orders
    }

    private fun escapeCsv(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"" + value.replace("\"", "\"\"") + "\""
        } else {
            value
        }
    }

    private fun parseCsvLine(line: String): List<String> {
        val tokens = mutableListOf<String>()
        val sb = StringBuilder()
        var inQuotes = false
        var i = 0

        while (i < line.length) {
            val c = line[i]
            when {
                c == '\"' -> {
                    if (inQuotes && i + 1 < line.length && line[i + 1] == '\"') {
                        sb.append('\"')
                        i++
                    } else {
                        inQuotes = !inQuotes
                    }
                }
                c == ',' && !inQuotes -> {
                    tokens.add(sb.toString())
                    sb.clear()
                }
                else -> {
                    sb.append(c)
                }
            }
            i++
        }
        tokens.add(sb.toString())
        return tokens
    }
}
