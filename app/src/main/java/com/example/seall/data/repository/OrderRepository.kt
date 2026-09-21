package com.example.seall.data.repository

import com.example.seall.data.local.OrderDao
import com.example.seall.data.model.Order
import kotlinx.coroutines.flow.Flow

class OrderRepository(private val orderDao: OrderDao) {

    val allOrders: Flow<List<Order>> = orderDao.getAllOrders()
    val unpaidOrders: Flow<List<Order>> = orderDao.getUnpaidOrders()
    val paidTotal: Flow<Double> = orderDao.getPaidTotal()
    val unpaidTotal: Flow<Double> = orderDao.getUnpaidTotal()
    val combinedTotal: Flow<Double> = orderDao.getCombinedTotal()
    val unpaidCount: Flow<Int> = orderDao.getUnpaidCount()

    fun getOrderById(id: Long): Flow<Order?> = orderDao.getOrderById(id)

    fun searchOrders(query: String): Flow<List<Order>> = orderDao.searchOrders(query)

    fun getOrdersByDateRange(startTime: Long, endTime: Long): Flow<List<Order>> =
        orderDao.getOrdersByDateRange(startTime, endTime)

    suspend fun insertOrder(order: Order): Long = orderDao.insertOrder(order)

    suspend fun updateOrder(order: Order) = orderDao.updateOrder(order)

    suspend fun deleteOrder(order: Order) = orderDao.deleteOrder(order)

    suspend fun deleteOrderById(id: Long) = orderDao.deleteOrderById(id)

    suspend fun togglePaidStatus(orderId: Long, isPaid: Boolean) =
        orderDao.updatePaymentStatus(orderId, isPaid)
}
