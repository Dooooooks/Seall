package com.example.seall.data.model

import org.json.JSONArray
import org.json.JSONObject

data class OrderItem(
    val stockId: Long? = null,
    val name: String,
    val price: Double,
    val quantity: Int = 1
) {
    fun toJson(): JSONObject {
        return JSONObject().apply {
            if (stockId != null) put("stockId", stockId)
            put("name", name)
            put("price", price)
            put("quantity", quantity)
        }
    }

    companion object {
        fun fromJson(json: JSONObject): OrderItem {
            val stockId = if (json.has("stockId") && !json.isNull("stockId")) json.getLong("stockId") else null
            val name = json.optString("name", "")
            val price = json.optDouble("price", 0.0)
            val quantity = json.optInt("quantity", 1)
            return OrderItem(stockId, name, price, quantity)
        }

        fun listToJson(items: List<OrderItem>): String {
            val array = JSONArray()
            for (item in items) {
                array.put(item.toJson())
            }
            return array.toString()
        }

        fun listFromJson(jsonStr: String): List<OrderItem> {
            if (jsonStr.isBlank()) return emptyList()
            return try {
                val array = JSONArray(jsonStr)
                val list = mutableListOf<OrderItem>()
                for (i in 0 until array.length()) {
                    list.add(fromJson(array.getJSONObject(i)))
                }
                list
            } catch (e: Exception) {
                emptyList()
            }
        }

        fun formatSummary(items: List<OrderItem>): String {
            if (items.isEmpty()) return ""
            return items.joinToString(", ") { "${it.quantity}x ${it.name}" }
        }

        fun totalCount(items: List<OrderItem>): Int {
            return items.sumOf { it.quantity }
        }

        fun totalPrice(items: List<OrderItem>): Double {
            return items.sumOf { it.price * it.quantity }
        }
    }
}
