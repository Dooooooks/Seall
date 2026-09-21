package com.example.seall.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stocks")
data class StockItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val price: Double,
    val quantity: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
