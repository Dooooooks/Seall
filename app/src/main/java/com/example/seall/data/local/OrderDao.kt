package com.example.seall.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.seall.data.model.Order
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE id = :id LIMIT 1")
    fun getOrderById(id: Long): Flow<Order?>

    @Query("SELECT * FROM orders WHERE isPaid = 0 ORDER BY createdAt DESC")
    fun getUnpaidOrders(): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE createdAt >= :startTime AND createdAt <= :endTime ORDER BY createdAt DESC")
    fun getOrdersByDateRange(startTime: Long, endTime: Long): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE customerName LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchOrders(query: String): Flow<List<Order>>

    @Query("SELECT COALESCE(SUM(price), 0.0) FROM orders WHERE isPaid = 1")
    fun getPaidTotal(): Flow<Double>

    @Query("SELECT COALESCE(SUM(price), 0.0) FROM orders WHERE isPaid = 0")
    fun getUnpaidTotal(): Flow<Double>

    @Query("SELECT COALESCE(SUM(price), 0.0) FROM orders")
    fun getCombinedTotal(): Flow<Double>

    @Query("SELECT COUNT(*) FROM orders WHERE isPaid = 0")
    fun getUnpaidCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<Order>): List<Long>

    @Update
    suspend fun updateOrder(order: Order)

    @Delete
    suspend fun deleteOrder(order: Order)

    @Query("DELETE FROM orders WHERE id = :id")
    suspend fun deleteOrderById(id: Long)

    @Query("DELETE FROM orders WHERE createdAt >= :startTime AND createdAt <= :endTime")
    suspend fun deleteOrdersInDateRange(startTime: Long, endTime: Long)

    @Query("UPDATE orders SET isPaid = :isPaid WHERE id = :id")
    suspend fun updatePaymentStatus(id: Long, isPaid: Boolean)
}
