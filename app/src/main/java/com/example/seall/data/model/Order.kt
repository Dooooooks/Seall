package com.example.seall.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Core Order data structure representing a sales transaction.
 *
 * @property id Unique identifier for the order (auto-generated)
 * @property customerName Name or identifier of the customer
 * @property price Price/amount of the order in decimal
 * @property isPaid Payment status (true if settled, false if pending)
 * @property itemsSummary Comma-separated summary of ordered items e.g. "2x Brownies, 1x Crinkles"
 * @property itemsJson Serialized JSON list of OrderItem objects
 * @property totalItemCount Total number of items ordered
 * @property createdAt Epoch millisecond timestamp of transaction creation
 */
@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerName: String,
    val price: Double,
    val isPaid: Boolean,
    val itemsSummary: String = "",
    val itemsJson: String = "",
    val totalItemCount: Int = 1,
    val createdAt: Long = System.currentTimeMillis()
)
