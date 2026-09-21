package com.example.seall.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.seall.data.model.StockIngredient
import kotlinx.coroutines.flow.Flow

@Dao
interface StockIngredientDao {

    @Query("SELECT * FROM stock_ingredients ORDER BY id ASC")
    fun getAllStockIngredients(): Flow<List<StockIngredient>>

    @Query("SELECT * FROM stock_ingredients WHERE stockId = :stockId ORDER BY id ASC")
    fun getIngredientsForStock(stockId: Long): Flow<List<StockIngredient>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockIngredient(ingredient: StockIngredient): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockIngredients(ingredients: List<StockIngredient>): List<Long>

    @Update
    suspend fun updateStockIngredient(ingredient: StockIngredient)

    @Delete
    suspend fun deleteStockIngredient(ingredient: StockIngredient)

    @Query("DELETE FROM stock_ingredients WHERE stockId = :stockId")
    suspend fun deleteIngredientsForStock(stockId: Long)
}
