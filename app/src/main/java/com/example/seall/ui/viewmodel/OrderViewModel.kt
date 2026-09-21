package com.example.seall.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.seall.data.local.SeallDatabase
import com.example.seall.data.model.Ingredient
import com.example.seall.data.model.Order
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

    fun submitOrder(customerName: String, price: Double, isPaid: Boolean) {
        viewModelScope.launch {
            val currentEdit = _editingOrder.value
            if (currentEdit != null) {
                val updated = currentEdit.copy(
                    customerName = customerName.trim(),
                    price = price,
                    isPaid = isPaid
                )
                repository.updateOrder(updated)
            } else {
                val newOrder = Order(
                    customerName = customerName.trim(),
                    price = price,
                    isPaid = isPaid,
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
    fun addStock(name: String, price: Double) {
        val trimmed = name.trim()
        if (trimmed.isNotBlank() && price > 0.0) {
            viewModelScope.launch {
                repository.insertStock(StockItem(name = trimmed, price = price))
            }
        }
    }

    fun updateStock(stock: StockItem, name: String, price: Double) {
        val trimmed = name.trim()
        if (trimmed.isNotBlank() && price > 0.0) {
            viewModelScope.launch {
                repository.updateStock(stock.copy(name = trimmed, price = price))
            }
        }
    }

    fun deleteStock(stock: StockItem) {
        viewModelScope.launch {
            repository.deleteStock(stock)
        }
    }

    // Ingredients Management
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
                    val repo = OrderRepository(db.orderDao(), db.stockDao(), db.ingredientDao())
                    return OrderViewModel(application, repo) as T
                }
            }
    }
}
