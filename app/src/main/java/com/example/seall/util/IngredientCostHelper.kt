package com.example.seall.util

import com.example.seall.data.model.Order
import com.example.seall.data.model.OrderItem
import com.example.seall.data.model.StockIngredient
import com.example.seall.data.model.StockItem
import java.util.Locale

data class IngredientUsageSummary(
    val ingredientName: String,
    val totalCost: Double,
    val totalItemCount: Int,
    val quantityDescriptions: List<String>,
    val stockNames: List<String>
)

data class ProductRecipeSummary(
    val stock: StockItem,
    val ingredients: List<StockIngredient>,
    val totalRecipeCost: Double,
    val profitPerUnit: Double
)

object IngredientCostHelper {

    /**
     * Resolves the list of [OrderItem] from an [Order].
     * First attempts to parse [Order.itemsJson].
     * If blank or empty, falls back to parsing [Order.itemsSummary] (e.g., "2x Brownies, 1x Crinkles").
     */
    fun resolveOrderItems(order: Order, stocks: List<StockItem>): List<OrderItem> {
        if (order.itemsJson.isNotBlank()) {
            val parsed = OrderItem.listFromJson(order.itemsJson)
            if (parsed.isNotEmpty()) return parsed
        }
        if (order.itemsSummary.isNotBlank()) {
            val parts = order.itemsSummary.split(",")
            val items = mutableListOf<OrderItem>()
            val regex = Regex("""^(\d+)\s*[xX]?\s+(.+)$""")
            for (part in parts) {
                val trimmed = part.trim()
                if (trimmed.isBlank()) continue
                val match = regex.find(trimmed)
                val (qty, name) = if (match != null) {
                    val q = match.groupValues[1].toIntOrNull() ?: 1
                    val n = match.groupValues[2].trim()
                    q to n
                } else {
                    1 to trimmed
                }
                val matchedStock = stocks.find { it.name.equals(name, ignoreCase = true) }
                    ?: stocks.find { it.name.contains(name, ignoreCase = true) || name.contains(it.name, ignoreCase = true) }
                items.add(
                    OrderItem(
                        stockId = matchedStock?.id,
                        name = matchedStock?.name ?: name,
                        price = matchedStock?.price ?: 0.0,
                        quantity = maxOf(1, qty)
                    )
                )
            }
            if (items.isNotEmpty()) return items
        }
        return emptyList()
    }

    /**
     * Finds the associated [StockItem] and its [StockIngredient] list for a given [OrderItem].
     */
    fun findIngredientsForOrderItem(
        item: OrderItem,
        stocks: List<StockItem>,
        stockIngredients: List<StockIngredient>
    ): Pair<StockItem?, List<StockIngredient>> {
        val stock = if (item.stockId != null) {
            stocks.find { it.id == item.stockId }
                ?: stocks.find { it.name.equals(item.name.trim(), ignoreCase = true) }
        } else {
            stocks.find { it.name.equals(item.name.trim(), ignoreCase = true) }
                ?: stocks.find {
                    it.name.contains(item.name.trim(), ignoreCase = true) ||
                        item.name.trim().contains(it.name, ignoreCase = true)
                }
        }

        val targetStockId = stock?.id ?: item.stockId
        val ingredients = if (targetStockId != null) {
            stockIngredients.filter { it.stockId == targetStockId }
        } else {
            emptyList()
        }
        return Pair(stock, ingredients)
    }

    /**
     * Calculates the total cost of ingredients for a single [Order].
     */
    fun calculateOrderIngredientsCost(
        order: Order,
        stocks: List<StockItem>,
        stockIngredients: List<StockIngredient>
    ): Double {
        val items = resolveOrderItems(order, stocks)
        return items.sumOf { item ->
            val (_, ingredients) = findIngredientsForOrderItem(item, stocks, stockIngredients)
            val unitIngredientCost = ingredients.sumOf { it.cost }
            unitIngredientCost * item.quantity
        }
    }

    /**
     * Calculates total ingredients cost across multiple orders.
     */
    fun calculateTotalOrdersIngredientsCost(
        orders: List<Order>,
        stocks: List<StockItem>,
        stockIngredients: List<StockIngredient>
    ): Double {
        return orders.sumOf { calculateOrderIngredientsCost(it, stocks, stockIngredients) }
    }

    /**
     * Aggregates usage details of individual ingredients across given orders.
     */
    fun computeIngredientUsages(
        orders: List<Order>,
        stocks: List<StockItem>,
        stockIngredients: List<StockIngredient>
    ): List<IngredientUsageSummary> {
        val usageMap = mutableMapOf<String, MutableUsage>()

        for (order in orders) {
            val items = resolveOrderItems(order, stocks)
            for (item in items) {
                val (stock, ingredients) = findIngredientsForOrderItem(item, stocks, stockIngredients)
                for (ing in ingredients) {
                    val key = ing.name.trim().lowercase(Locale.getDefault())
                    val entry = usageMap.getOrPut(key) {
                        MutableUsage(ingredientName = ing.name.trim())
                    }
                    entry.totalCost += ing.cost * item.quantity
                    entry.totalItemCount += item.quantity
                    val qtyDesc = "${ing.quantity.ifBlank { "1 pc" }} × ${item.quantity}"
                    if (!entry.quantities.contains(qtyDesc)) {
                        entry.quantities.add(qtyDesc)
                    }
                    val stockName = stock?.name ?: item.name
                    if (!entry.stockNames.contains(stockName)) {
                        entry.stockNames.add(stockName)
                    }
                }
            }
        }

        return usageMap.values.map {
            IngredientUsageSummary(
                ingredientName = it.ingredientName,
                totalCost = it.totalCost,
                totalItemCount = it.totalItemCount,
                quantityDescriptions = it.quantities,
                stockNames = it.stockNames
            )
        }.sortedByDescending { it.totalCost }
    }

    /**
     * Computes the recipe summary per product in stock.
     */
    fun computeProductRecipeSummaries(
        stocks: List<StockItem>,
        stockIngredients: List<StockIngredient>
    ): List<ProductRecipeSummary> {
        return stocks.map { stock ->
            val ingredients = stockIngredients.filter { it.stockId == stock.id }
            val totalCost = ingredients.sumOf { it.cost }
            ProductRecipeSummary(
                stock = stock,
                ingredients = ingredients,
                totalRecipeCost = totalCost,
                profitPerUnit = stock.price - totalCost
            )
        }.sortedBy { it.stock.name }
    }

    private class MutableUsage(
        val ingredientName: String,
        var totalCost: Double = 0.0,
        var totalItemCount: Int = 0,
        val quantities: MutableList<String> = mutableListOf(),
        val stockNames: MutableList<String> = mutableListOf()
    )
}
