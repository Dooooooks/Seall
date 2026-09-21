package com.example.seall.data.repository

import com.example.seall.data.local.IngredientDao
import com.example.seall.data.local.OrderDao
import com.example.seall.data.local.StockDao
import com.example.seall.data.local.StockIngredientDao
import com.example.seall.data.model.Ingredient
import com.example.seall.data.model.Order
import com.example.seall.data.model.StockIngredient
import com.example.seall.data.model.StockItem
import kotlinx.coroutines.flow.Flow

class OrderRepository(
    private val orderDao: OrderDao,
    private val stockDao: StockDao,
    private val ingredientDao: IngredientDao,
    private val stockIngredientDao: StockIngredientDao
) {

    val allOrders: Flow<List<Order>> = orderDao.getAllOrders()
    val unpaidOrders: Flow<List<Order>> = orderDao.getUnpaidOrders()
    val paidTotal: Flow<Double> = orderDao.getPaidTotal()
    val unpaidTotal: Flow<Double> = orderDao.getUnpaidTotal()
    val combinedTotal: Flow<Double> = orderDao.getCombinedTotal()
    val unpaidCount: Flow<Int> = orderDao.getUnpaidCount()

    // Stocks
    val allStocks: Flow<List<StockItem>> = stockDao.getAllStocks()

    suspend fun insertStock(stock: StockItem): Long = stockDao.insertStock(stock)

    suspend fun insertStocks(stocks: List<StockItem>): List<Long> = stockDao.insertStocks(stocks)

    suspend fun updateStock(stock: StockItem) = stockDao.updateStock(stock)

    suspend fun deleteStock(stock: StockItem) = stockDao.deleteStock(stock)

    suspend fun deleteStockById(id: Long) = stockDao.deleteStockById(id)

    // Stock Ingredients (Recipe per stock product)
    val allStockIngredients: Flow<List<StockIngredient>> = stockIngredientDao.getAllStockIngredients()

    fun getStockIngredientsForStock(stockId: Long): Flow<List<StockIngredient>> =
        stockIngredientDao.getIngredientsForStock(stockId)

    suspend fun insertStockIngredient(ingredient: StockIngredient): Long =
        stockIngredientDao.insertStockIngredient(ingredient)

    suspend fun updateStockIngredient(ingredient: StockIngredient) =
        stockIngredientDao.updateStockIngredient(ingredient)

    suspend fun deleteStockIngredient(ingredient: StockIngredient) =
        stockIngredientDao.deleteStockIngredient(ingredient)

    suspend fun deleteIngredientsForStock(stockId: Long) =
        stockIngredientDao.deleteIngredientsForStock(stockId)

    // General Expenses / Ingredients
    val allIngredients: Flow<List<Ingredient>> = ingredientDao.getAllIngredients()

    fun getIngredientsByDateRange(startTime: Long, endTime: Long): Flow<List<Ingredient>> =
        ingredientDao.getIngredientsByDateRange(startTime, endTime)

    suspend fun insertIngredient(ingredient: Ingredient): Long = ingredientDao.insertIngredient(ingredient)

    suspend fun updateIngredient(ingredient: Ingredient) = ingredientDao.updateIngredient(ingredient)

    suspend fun deleteIngredient(ingredient: Ingredient) = ingredientDao.deleteIngredient(ingredient)

    suspend fun deleteIngredientById(id: Long) = ingredientDao.deleteIngredientById(id)

    suspend fun deleteIngredientsForDay(startTime: Long, endTime: Long) =
        ingredientDao.deleteIngredientsForDay(startTime, endTime)

    fun getOrderById(id: Long): Flow<Order?> = orderDao.getOrderById(id)

    fun searchOrders(query: String): Flow<List<Order>> = orderDao.searchOrders(query)

    fun getOrdersByDateRange(startTime: Long, endTime: Long): Flow<List<Order>> =
        orderDao.getOrdersByDateRange(startTime, endTime)

    suspend fun insertOrder(order: Order): Long = orderDao.insertOrder(order)

    suspend fun insertOrders(orders: List<Order>): List<Long> = orderDao.insertOrders(orders)

    suspend fun updateOrder(order: Order) = orderDao.updateOrder(order)

    suspend fun deleteOrder(order: Order) = orderDao.deleteOrder(order)

    suspend fun deleteOrderById(id: Long) = orderDao.deleteOrderById(id)

    suspend fun deleteOrdersInDateRange(startTime: Long, endTime: Long) =
        orderDao.deleteOrdersInDateRange(startTime, endTime)

    suspend fun togglePaidStatus(orderId: Long, isPaid: Boolean) =
        orderDao.updatePaymentStatus(orderId, isPaid)
}
