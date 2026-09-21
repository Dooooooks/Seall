package com.example.seall.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.seall.data.local.SeallDatabase
import com.example.seall.data.model.Ingredient
import com.example.seall.data.model.Order
import com.example.seall.data.model.StockIngredient
import com.example.seall.data.model.StockItem
import com.example.seall.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OrderViewModel(
    application: Application,
    private val repository: OrderRepository
) : AndroidViewModel(application) {

    // Raw streams from DB
    val orders: StateFlow<List<Order>> = repository.allOrders
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val unpaidOrders: StateFlow<List<Order>> = repository.unpaidOrders
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val paidTotal: StateFlow<Double> = repository.paidTotal
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    val unpaidTotal: StateFlow<Double> = repository.unpaidTotal
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    val combinedTotal: StateFlow<Double> = repository.combinedTotal
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    val unpaidCount: StateFlow<Int> = repository.unpaidCount
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val stocks: StateFlow<List<StockItem>> = repository.allStocks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val stockIngredients: StateFlow<List<StockIngredient>> = repository.allStockIngredients
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val ingredients: StateFlow<List<Ingredient>> = repository.allIngredients
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Rapid-Entry Wizard & Edit States
    private val _isWizardOpen = MutableStateFlow(false)
    val isWizardOpen: StateFlow<Boolean> = _isWizardOpen.asStateFlow()

    private val _editingOrder = MutableStateFlow<Order?>(null)
    val editingOrder: StateFlow<Order?> = _editingOrder.asStateFlow()

    fun deleteOrdersForDay(startOfDay: Long, endOfDay: Long) {
        viewModelScope.launch {
            repository.deleteOrdersInDateRange(startOfDay, endOfDay)
            repository.deleteIngredientsForDay(startOfDay, endOfDay)
        }
    }

    // Wizard Controls
    fun openWizard(orderToEdit: Order? = null) {
        _editingOrder.value = orderToEdit
        _isWizardOpen.value = true
    }

    fun closeWizard() {
        _isWizardOpen.value = false
        _editingOrder.value = null
    }

    fun submitOrder(
        customerName: String,
        price: Double,
        isPaid: Boolean,
        itemsSummary: String = "",
        itemsJson: String = "",
        totalItemCount: Int = 1
    ) {
        viewModelScope.launch {
            val currentEdit = _editingOrder.value
            if (currentEdit != null) {
                val updated = currentEdit.copy(
                    customerName = customerName.trim(),
                    price = price,
                    isPaid = isPaid,
                    itemsSummary = if (itemsSummary.isNotBlank()) itemsSummary else currentEdit.itemsSummary,
                    itemsJson = if (itemsJson.isNotBlank()) itemsJson else currentEdit.itemsJson,
                    totalItemCount = if (itemsSummary.isNotBlank()) totalItemCount else currentEdit.totalItemCount
                )
                repository.updateOrder(updated)
            } else {
                val newOrder = Order(
                    customerName = customerName.trim(),
                    price = price,
                    isPaid = isPaid,
                    itemsSummary = itemsSummary,
                    itemsJson = itemsJson,
                    totalItemCount = totalItemCount,
                    createdAt = System.currentTimeMillis()
                )
                repository.insertOrder(newOrder)
            }
            closeWizard()
        }
    }

    fun addOrder(customerName: String, price: Double, isPaid: Boolean) {
        submitOrder(customerName, price, isPaid)
    }

    fun togglePayment(order: Order) {
        viewModelScope.launch {
            repository.togglePaidStatus(order.id, !order.isPaid)
        }
    }

    fun deleteOrder(order: Order) {
        viewModelScope.launch {
            repository.deleteOrder(order)
        }
    }

    fun updateCustomerName(order: Order, newName: String) {
        val trimmed = newName.trim()
        if (trimmed.isNotBlank() && trimmed != order.customerName) {
            viewModelScope.launch {
                repository.updateOrder(order.copy(customerName = trimmed))
            }
        }
    }

    fun updateOrderPrice(order: Order, newPrice: Double) {
        if (newPrice > 0.0 && newPrice != order.price) {
            viewModelScope.launch {
                repository.updateOrder(order.copy(price = newPrice))
            }
        }
    }

    fun importOrders(ordersToImport: List<Order>, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.insertOrders(ordersToImport)
            onComplete()
        }
    }

    // Stocks Management
    fun addStock(name: String, price: Double, quantity: Int = 0) {
        val trimmed = name.trim()
        if (trimmed.isNotBlank() && price > 0.0) {
            viewModelScope.launch {
                repository.insertStock(StockItem(name = trimmed, price = price, quantity = maxOf(0, quantity)))
            }
        }
    }

    fun updateStock(stock: StockItem, name: String, price: Double, quantity: Int = stock.quantity) {
        val trimmed = name.trim()
        if (trimmed.isNotBlank() && price > 0.0) {
            viewModelScope.launch {
                repository.updateStock(stock.copy(name = trimmed, price = price, quantity = maxOf(0, quantity)))
            }
        }
    }

    fun updateStockQuantity(stock: StockItem, quantity: Int) {
        viewModelScope.launch {
            repository.updateStock(stock.copy(quantity = maxOf(0, quantity)))
        }
    }

    fun adjustStockQuantity(stock: StockItem, delta: Int) {
        val newQuantity = maxOf(0, stock.quantity + delta)
        viewModelScope.launch {
            repository.updateStock(stock.copy(quantity = newQuantity))
        }
    }

    fun deleteStock(stock: StockItem) {
        viewModelScope.launch {
            repository.deleteStock(stock)
        }
    }

    // Stock Ingredients (Recipe per Stock Item)
    fun addStockIngredient(stockId: Long, name: String, quantity: String, cost: Double = 0.0) {
        val trimmedName = name.trim()
        val trimmedQty = quantity.trim().ifBlank { "1" }
        if (trimmedName.isNotBlank()) {
            viewModelScope.launch {
                repository.insertStockIngredient(
                    StockIngredient(
                        stockId = stockId,
                        name = trimmedName,
                        quantity = trimmedQty,
                        cost = cost
                    )
                )
            }
        }
    }

    fun updateStockIngredient(ingredient: StockIngredient, name: String, quantity: String, cost: Double) {
        val trimmedName = name.trim()
        val trimmedQty = quantity.trim().ifBlank { "1" }
        if (trimmedName.isNotBlank()) {
            viewModelScope.launch {
                repository.updateStockIngredient(
                    ingredient.copy(
                        name = trimmedName,
                        quantity = trimmedQty,
                        cost = cost
                    )
                )
            }
        }
    }

    fun deleteStockIngredient(ingredient: StockIngredient) {
        viewModelScope.launch {
            repository.deleteStockIngredient(ingredient)
        }
    }

    // General Expenses / Ingredients Management
    fun addIngredient(name: String, price: Double) {
        val trimmed = name.trim()
        if (trimmed.isNotBlank() && price > 0.0) {
            viewModelScope.launch {
                repository.insertIngredient(Ingredient(name = trimmed, price = price))
            }
        }
    }

    fun updateIngredient(ingredient: Ingredient, name: String, price: Double) {
        val trimmed = name.trim()
        if (trimmed.isNotBlank() && price > 0.0) {
            viewModelScope.launch {
                repository.updateIngredient(ingredient.copy(name = trimmed, price = price))
            }
        }
    }

    fun deleteIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            repository.deleteIngredient(ingredient)
        }
    }

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val db = SeallDatabase.getInstance(application)
                    val repo = OrderRepository(
                        db.orderDao(),
                        db.stockDao(),
                        db.ingredientDao(),
                        db.stockIngredientDao()
                    )
                    return OrderViewModel(application, repo) as T
                }
            }
    }
}
