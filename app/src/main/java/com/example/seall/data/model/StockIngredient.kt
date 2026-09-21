package com.example.seall.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents an ingredient tied to a specific stock product (recipe item).
 * e.g. Brownies -> "Cocoa Powder", quantity: "2 packs", cost: ₱40.00
 *
 * @property id Unique identifier
 * @property stockId ID of the parent StockItem
 * @property name Name of the ingredient (e.g. Cocoa Powder, Eggs)
 * @property quantity Quantity or number of items (e.g. "3", "2 pcs", "250g")
 * @property cost Cost in decimal (₱)
 */
@Entity(
    tableName = "stock_ingredients",
    foreignKeys = [
        ForeignKey(
            entity = StockItem::class,
            parentColumns = ["id"],
            childColumns = ["stockId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("stockId")]
)
data class StockIngredient(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val stockId: Long,
    val name: String,
    val quantity: String,
    val cost: Double = 0.0
)
