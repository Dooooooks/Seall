package com.example.seall.util

import com.example.seall.data.model.Order
import com.example.seall.data.model.StockIngredient
import com.example.seall.data.model.StockItem
import org.junit.Assert.assertEquals
import org.junit.Test

class IngredientCostHelperTest {

    private val brownieStock = StockItem(id = 1L, name = "Brownies", price = 50.0, quantity = 10)
    private val crinkleStock = StockItem(id = 2L, name = "Crinkles", price = 30.0, quantity = 20)
    private val stocks = listOf(brownieStock, crinkleStock)

    private val stockIngredients = listOf(
        StockIngredient(id = 1L, stockId = 1L, name = "Cocoa Powder", quantity = "1 pack", cost = 15.0),
        StockIngredient(id = 2L, stockId = 1L, name = "Eggs", quantity = "2 pcs", cost = 10.0),
        StockIngredient(id = 3L, stockId = 2L, name = "Powdered Sugar", quantity = "100g", cost = 5.0)
    )

    @Test
    fun testCalculateOrderIngredientsCost_withItemsJson() {
        // Brownies recipe cost = 15 + 10 = 25. Order 2 brownies -> 50.0
        val itemsJson = """[{"stockId":1,"name":"Brownies","price":50.0,"quantity":2}]"""
        val order = Order(customerName = "John", price = 100.0, isPaid = true, itemsJson = itemsJson)

        val cost = IngredientCostHelper.calculateOrderIngredientsCost(order, stocks, stockIngredients)
        assertEquals(50.0, cost, 0.001)
    }

    @Test
    fun testCalculateOrderIngredientsCost_fallbackItemsSummary() {
        // 2x Brownies (2*25 = 50) + 1x Crinkles (1*5 = 5) = 55.0
        val order = Order(customerName = "Jane", price = 130.0, isPaid = true, itemsSummary = "2x Brownies, 1x Crinkles")

        val cost = IngredientCostHelper.calculateOrderIngredientsCost(order, stocks, stockIngredients)
        assertEquals(55.0, cost, 0.001)
    }

    @Test
    fun testComputeIngredientUsages() {
        val itemsJson = """[{"stockId":1,"name":"Brownies","price":50.0,"quantity":3}]"""
        val order = Order(customerName = "Alice", price = 150.0, isPaid = true, itemsJson = itemsJson)

        val usages = IngredientCostHelper.computeIngredientUsages(listOf(order), stocks, stockIngredients)
        assertEquals(2, usages.size)

        val cocoa = usages.find { it.ingredientName == "Cocoa Powder" }
        assertEquals(45.0, cocoa?.totalCost ?: 0.0, 0.001)

        val eggs = usages.find { it.ingredientName == "Eggs" }
        assertEquals(30.0, eggs?.totalCost ?: 0.0, 0.001)
    }

    @Test
    fun testComputeProductRecipeSummaries() {
        val summaries = IngredientCostHelper.computeProductRecipeSummaries(stocks, stockIngredients)
        assertEquals(2, summaries.size)

        val brownie = summaries.find { it.stock.id == 1L }
        assertEquals(25.0, brownie?.totalRecipeCost ?: 0.0, 0.001)
        assertEquals(25.0, brownie?.profitPerUnit ?: 0.0, 0.001)

        val crinkle = summaries.find { it.stock.id == 2L }
        assertEquals(5.0, crinkle?.totalRecipeCost ?: 0.0, 0.001)
        assertEquals(25.0, crinkle?.profitPerUnit ?: 0.0, 0.001)
    }

    @Test
    fun testCalculateTotalOrdersIngredientsCost_multipleOrders() {
        val order1 = Order(customerName = "A", price = 100.0, isPaid = true, itemsSummary = "2x Brownies") // 2*25 = 50
        val order2 = Order(customerName = "B", price = 60.0, isPaid = true, itemsSummary = "2x Crinkles") // 2*5 = 10
        val order3 = Order(customerName = "C", price = 20.0, isPaid = false, itemsSummary = "") // no items -> 0

        val totalCost = IngredientCostHelper.calculateTotalOrdersIngredientsCost(
            listOf(order1, order2, order3),
            stocks,
            stockIngredients
        )
        assertEquals(60.0, totalCost, 0.001)
    }

    @Test
    fun testProductWithoutIngredients() {
        val cookieStock = StockItem(id = 3L, name = "Plain Cookie", price = 15.0, quantity = 5)
        val allStocks = stocks + cookieStock
        val order = Order(customerName = "D", price = 15.0, isPaid = true, itemsSummary = "1x Plain Cookie")

        val cost = IngredientCostHelper.calculateOrderIngredientsCost(order, allStocks, stockIngredients)
        assertEquals(0.0, cost, 0.001)
    }
}
