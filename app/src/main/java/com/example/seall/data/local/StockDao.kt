package com.example.seall.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.seall.data.model.StockItem
import kotlinx.coroutines.flow.Flow

@Dao
interface StockDao {

    @Query("SELECT * FROM stocks ORDER BY name ASC")
    fun getAllStocks(): Flow<List<StockItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStock(stock: StockItem): Long

    @Update
    suspend fun updateStock(stock: StockItem)

    @Delete
    suspend fun deleteStock(stock: StockItem)

    @Query("DELETE FROM stocks WHERE id = :id")
    suspend fun deleteStockById(id: Long)
}
