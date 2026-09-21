package com.example.seall.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.seall.data.local.SeallDatabase
import com.example.seall.data.model.Order
import com.example.seall.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

enum class PaymentFilter {
    ALL, PAID, UNPAID
}

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

    // UI Search & Filter States
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _paymentFilter = MutableStateFlow(PaymentFilter.ALL)
    val paymentFilter: StateFlow<PaymentFilter> = _paymentFilter.asStateFlow()

    private val _selectedDateMillis = MutableStateFlow<Long?>(null)
    val selectedDateMillis: StateFlow<Long?> = _selectedDateMillis.asStateFlow()

    // Rapid-Entry Wizard & Edit States
    private val _isWizardOpen = MutableStateFlow(false)
    val isWizardOpen: StateFlow<Boolean> = _isWizardOpen.asStateFlow()

    private val _editingOrder = MutableStateFlow<Order?>(null)
    val editingOrder: StateFlow<Order?> = _editingOrder.asStateFlow()

    // Combined Reactive Filtered Orders
    val filteredOrders: StateFlow<List<Order>> = combine(
        orders,
        _searchQuery,
        _paymentFilter,
        _selectedDateMillis
    ) { orderList, query, filter, dateMillis ->
        orderList.filter { order ->
            // Search filter
            val matchesQuery = query.isBlank() ||
                order.customerName.contains(query, ignoreCase = true) ||
                String.format("%.2f", order.price).contains(query)

            // Payment filter
            val matchesPayment = when (filter) {
                PaymentFilter.ALL -> true
                PaymentFilter.PAID -> order.isPaid
                PaymentFilter.UNPAID -> !order.isPaid
            }

            // Date filter
            val matchesDate = if (dateMillis == null) {
                true
            } else {
                val calOrder = Calendar.getInstance().apply { timeInMillis = order.createdAt }
                val calTarget = Calendar.getInstance().apply { timeInMillis = dateMillis }
                calOrder.get(Calendar.YEAR) == calTarget.get(Calendar.YEAR) &&
                    calOrder.get(Calendar.DAY_OF_YEAR) == calTarget.get(Calendar.DAY_OF_YEAR)
            }

            matchesQuery && matchesPayment && matchesDate
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

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

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setPaymentFilter(filter: PaymentFilter) {
        _paymentFilter.value = filter
    }

    fun setSelectedDateMillis(dateMillis: Long?) {
        _selectedDateMillis.value = dateMillis
    }

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val db = SeallDatabase.getInstance(application)
                    val repo = OrderRepository(db.orderDao())
                    return OrderViewModel(application, repo) as T
                }
            }
    }
}
