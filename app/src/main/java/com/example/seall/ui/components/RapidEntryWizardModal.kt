package com.example.seall.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.mutableStateMapOf
import com.example.seall.data.model.Order
import com.example.seall.data.model.OrderItem
import com.example.seall.data.model.StockItem
import com.example.seall.ui.theme.SeallDarkContrast
import com.example.seall.ui.theme.SeallPaidGreen
import com.example.seall.ui.theme.SeallPrimary
import com.example.seall.ui.theme.SeallUnpaidAmber
import java.util.Locale

@Composable
fun RapidEntryWizardModal(
    isOpen: Boolean,
    editingOrder: Order? = null,
    stocks: List<StockItem> = emptyList(),
    onDismiss: () -> Unit,
    onSubmit: (
        name: String,
        price: Double,
        isPaid: Boolean,
        itemsSummary: String,
        itemsJson: String,
        totalItemCount: Int
    ) -> Unit
) {
    if (!isOpen) return

    var currentStep by remember(editingOrder) { mutableIntStateOf(1) }
    var customerName by remember(editingOrder) { mutableStateOf(editingOrder?.customerName ?: "") }
    var priceInput by remember(editingOrder) {
        mutableStateOf(editingOrder?.let { String.format(Locale.US, "%.2f", it.price) } ?: "")
    }
    var nameError by remember { mutableStateOf(false) }
    var priceError by remember { mutableStateOf(false) }

    // Dynamic item quantities (StockItem id -> quantity)
    val itemQuantities = remember(editingOrder) {
        val initialMap = mutableStateMapOf<Long, Int>()
        if (editingOrder != null && editingOrder.itemsJson.isNotBlank()) {
            val parsed = OrderItem.listFromJson(editingOrder.itemsJson)
            parsed.forEach { item ->
                if (item.stockId != null) {
                    initialMap[item.stockId] = item.quantity
                }
            }
        }
        initialMap
    }

    val selectedOrderItems = remember(itemQuantities, stocks) {
        itemQuantities.filter { it.value > 0 }.mapNotNull { (stockId, qty) ->
            stocks.find { it.id == stockId }?.let { stock ->
                OrderItem(
                    stockId = stock.id,
                    name = stock.name,
                    price = stock.price,
                    quantity = qty
                )
            }
        }
    }

    val computedItemsSummary = remember(selectedOrderItems) {
        OrderItem.formatSummary(selectedOrderItems)
    }

    val computedTotalItemCount = remember(selectedOrderItems) {
        val sum = OrderItem.totalCount(selectedOrderItems)
        if (sum > 0) sum else 1
    }

    val nameFocusRequester = remember { FocusRequester() }
    val priceFocusRequester = remember { FocusRequester() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                )
                .systemBarsPadding()
                .imePadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { /* prevent click through to background */ }
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp)
                ) {
                    // Header with Step Indicator and Cancel/Back Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (currentStep > 1) {
                            IconButton(
                                onClick = {
                                    if (currentStep > 1) currentStep--
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Previous Step",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.size(36.dp))
                        }

                        // Step Pills
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            (1..3).forEach { step ->
                                val isCurrent = step == currentStep
                                val isPassed = step < currentStep
                                Box(
                                    modifier = Modifier
                                        .size(if (isCurrent) 28.dp else 22.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isCurrent -> SeallPrimary
                                                isPassed -> SeallPrimary.copy(alpha = 0.35f)
                                                else -> MaterialTheme.colorScheme.surfaceVariant
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$step",
                                        fontSize = if (isCurrent) 13.sp else 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCurrent) SeallDarkContrast else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Modal",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Sequential Animated Steps
                    AnimatedContent(
                        targetState = currentStep,
                        transitionSpec = {
                            if (targetState > initialState) {
                                slideInHorizontally { width -> width } togetherWith slideOutHorizontally { width -> -width }
                            } else {
                                slideInHorizontally { width -> -width } togetherWith slideOutHorizontally { width -> width }
                            }
                        },
                        label = "WizardStepTransition"
                    ) { step ->
                        when (step) {
                            1 -> {
                                // --- STEP 1: Customer Name ---
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    Text(
                                        text = if (editingOrder != null) "Edit Customer Name" else "Customer Name",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Type customer name, then tap Next or press Enter.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    OutlinedTextField(
                                        value = customerName,
                                        onValueChange = {
                                            customerName = it
                                            if (it.isNotBlank()) nameError = false
                                        },
                                        label = { Text("Customer Name") },
                                        isError = nameError,
                                        supportingText = if (nameError) {
                                            { Text("Customer name is required", color = MaterialTheme.colorScheme.error) }
                                        } else null,
                                        singleLine = true,
                                        leadingIcon = {
                                            Icon(Icons.Default.Person, contentDescription = null, tint = SeallPrimary)
                                        },
                                        keyboardOptions = KeyboardOptions(
                                            capitalization = KeyboardCapitalization.Words,
                                            imeAction = ImeAction.Next
                                        ),
                                        keyboardActions = KeyboardActions(
                                            onNext = {
                                                if (customerName.trim().isNotBlank()) {
                                                    currentStep = 2
                                                } else {
                                                    nameError = true
                                                }
                                            }
                                        ),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = SeallPrimary,
                                            focusedLabelColor = SeallPrimary
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .focusRequester(nameFocusRequester)
                                    )

                                    LaunchedEffect(Unit) {
                                        nameFocusRequester.requestFocus()
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))

                                    Button(
                                        onClick = {
                                            if (customerName.trim().isNotBlank()) {
                                                currentStep = 2
                                            } else {
                                                nameError = true
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = SeallPrimary,
                                            contentColor = SeallDarkContrast
                                        )
                                    ) {
                                        Text(
                                            text = "Next: Choose Items & Price",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                                    }
                                }
                            }

                            2 -> {
                                // --- STEP 2: Items & Price Entry ---
                                val scrollState = rememberScrollState()
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .verticalScroll(scrollState)
                                ) {
                                    Text(
                                        text = "Select Items & Price",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "For: $customerName",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = SeallPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    if (stocks.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(14.dp))
                                        Text(
                                            text = "Add Items from Stocks:",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))

                                        var isStockDropdownExpanded by remember { mutableStateOf(false) }

                                        // Product Selector Dropdown Button
                                        Box(modifier = Modifier.fillMaxWidth()) {
                                            Surface(
                                                onClick = { isStockDropdownExpanded = true },
                                                shape = RoundedCornerShape(12.dp),
                                                border = androidx.compose.foundation.BorderStroke(
                                                    1.dp,
                                                    if (isStockDropdownExpanded) SeallPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                                ),
                                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 14.dp, vertical = 13.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.ShoppingCart,
                                                            contentDescription = null,
                                                            tint = SeallPrimary,
                                                            modifier = Modifier.size(20.dp)
                                                        )
                                                        Text(
                                                            text = if (selectedOrderItems.isNotEmpty()) {
                                                                "${selectedOrderItems.size} products added (${computedTotalItemCount} pcs)"
                                                            } else {
                                                                "Select product to add..."
                                                            },
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            fontWeight = if (selectedOrderItems.isNotEmpty()) FontWeight.SemiBold else FontWeight.Normal,
                                                            color = if (selectedOrderItems.isNotEmpty()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                    Icon(
                                                        imageVector = if (isStockDropdownExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                                        contentDescription = if (isStockDropdownExpanded) "Close dropdown" else "Open dropdown",
                                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }

                                            // Centered product picker Dialog
                                            if (isStockDropdownExpanded) {
                                                Dialog(
                                                    onDismissRequest = { isStockDropdownExpanded = false },
                                                    properties = DialogProperties(usePlatformDefaultWidth = false)
                                                ) {
                                                    Card(
                                                        shape = RoundedCornerShape(16.dp),
                                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                                        modifier = Modifier
                                                            .fillMaxWidth(0.92f)
                                                            .wrapContentHeight()
                                                    ) {
                                                // Dropdown Header with Done Button
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 14.dp, vertical = 6.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = "Select Products",
                                                        style = MaterialTheme.typography.labelMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                    TextButton(
                                                        onClick = { isStockDropdownExpanded = false },
                                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                                    ) {
                                                        Text("Done", fontWeight = FontWeight.Bold, color = SeallPrimary)
                                                    }
                                                }

                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(1.dp)
                                                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                                                )

                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .heightIn(max = 400.dp)
                                                        .verticalScroll(rememberScrollState())
                                                ) {
                                                stocks.forEach { stock ->
                                                    val currentQty = itemQuantities[stock.id] ?: 0
                                                    val maxAvailable = stock.quantity
                                                    val isOutOfStock = maxAvailable <= 0
                                                    val canAddMore = maxAvailable > 0 && currentQty < maxAvailable

                                                    Surface(
                                                        color = if (currentQty > 0) SeallPrimary.copy(alpha = 0.09f) else Color.Transparent,
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(horizontal = 14.dp, vertical = 8.dp),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            // Product info (tap to add 1 if stock allows)
                                                            Column(
                                                                modifier = Modifier
                                                                    .weight(1f)
                                                                    .clickable(enabled = canAddMore) {
                                                                        val newQty = currentQty + 1
                                                                        itemQuantities[stock.id] = newQty
                                                                        val updatedItems = itemQuantities.filter { it.value > 0 }.mapNotNull { (sId, q) ->
                                                                            stocks.find { it.id == sId }?.let { s -> OrderItem(s.id, s.name, s.price, q) }
                                                                        }
                                                                        val sum = OrderItem.totalPrice(updatedItems)
                                                                        priceInput = if (sum % 1.0 == 0.0) sum.toLong().toString() else String.format(Locale.US, "%.2f", sum)
                                                                        priceError = false
                                                                    }
                                                                    .padding(end = 8.dp)
                                                            ) {
                                                                Text(
                                                                    text = stock.name,
                                                                    style = MaterialTheme.typography.bodyMedium,
                                                                    fontWeight = if (currentQty > 0) FontWeight.Bold else FontWeight.SemiBold,
                                                                    color = if (isOutOfStock) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface
                                                                )
                                                                Row(
                                                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                                    verticalAlignment = Alignment.CenterVertically
                                                                ) {
                                                                    Text(
                                                                        text = String.format(Locale.US, "₱%.2f each", stock.price),
                                                                        style = MaterialTheme.typography.bodySmall,
                                                                        color = if (isOutOfStock) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f) else SeallPrimary,
                                                                        fontWeight = FontWeight.Medium
                                                                    )
                                                                    Text(
                                                                        text = when {
                                                                            isOutOfStock -> "• Out of stock (0 pcs)"
                                                                            currentQty >= maxAvailable -> "• Max stock (${maxAvailable} pcs)"
                                                                            else -> "• Stock: ${stock.quantity} pcs"
                                                                        },
                                                                        style = MaterialTheme.typography.bodySmall,
                                                                        color = when {
                                                                            isOutOfStock -> MaterialTheme.colorScheme.error
                                                                            currentQty >= maxAvailable -> SeallUnpaidAmber
                                                                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                                                                        },
                                                                        fontWeight = if (isOutOfStock || currentQty >= maxAvailable) FontWeight.SemiBold else FontWeight.Normal
                                                                    )
                                                                }
                                                            }

                                                            // Stepper controls (+ and -) inside dropdown
                                                            Row(
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                            ) {
                                                                // Decrement button
                                                                IconButton(
                                                                    onClick = {
                                                                        if (currentQty > 0) {
                                                                            val newQty = currentQty - 1
                                                                            if (newQty <= 0) {
                                                                                itemQuantities.remove(stock.id)
                                                                            } else {
                                                                                itemQuantities[stock.id] = newQty
                                                                            }
                                                                            val updatedItems = itemQuantities.filter { it.value > 0 }.mapNotNull { (sId, q) ->
                                                                                stocks.find { it.id == sId }?.let { s -> OrderItem(s.id, s.name, s.price, q) }
                                                                            }
                                                                            val sum = OrderItem.totalPrice(updatedItems)
                                                                            priceInput = if (updatedItems.isNotEmpty()) {
                                                                                if (sum % 1.0 == 0.0) sum.toLong().toString() else String.format(Locale.US, "%.2f", sum)
                                                                            } else {
                                                                                ""
                                                                            }
                                                                            priceError = false
                                                                        }
                                                                    },
                                                                    enabled = currentQty > 0,
                                                                    modifier = Modifier
                                                                        .size(30.dp)
                                                                        .clip(CircleShape)
                                                                        .background(
                                                                            if (currentQty > 0) MaterialTheme.colorScheme.surfaceVariant
                                                                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                                                        )
                                                                ) {
                                                                    Icon(
                                                                        imageVector = Icons.Default.Remove,
                                                                        contentDescription = "Decrease",
                                                                        tint = if (currentQty > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                                                        modifier = Modifier.size(15.dp)
                                                                    )
                                                                }

                                                                Text(
                                                                    text = "$currentQty",
                                                                    style = MaterialTheme.typography.titleSmall,
                                                                    fontWeight = if (currentQty > 0) FontWeight.Bold else FontWeight.Normal,
                                                                    color = if (currentQty > 0) SeallPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                                    modifier = Modifier.width(22.dp),
                                                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                                                )

                                                                // Increment button (disabled if out of stock or max reached)
                                                                IconButton(
                                                                    onClick = {
                                                                        if (canAddMore) {
                                                                            val newQty = currentQty + 1
                                                                            itemQuantities[stock.id] = newQty
                                                                            val updatedItems = itemQuantities.filter { it.value > 0 }.mapNotNull { (sId, q) ->
                                                                                stocks.find { it.id == sId }?.let { s -> OrderItem(s.id, s.name, s.price, q) }
                                                                            }
                                                                            val sum = OrderItem.totalPrice(updatedItems)
                                                                            priceInput = if (sum % 1.0 == 0.0) sum.toLong().toString() else String.format(Locale.US, "%.2f", sum)
                                                                            priceError = false
                                                                        }
                                                                    },
                                                                    enabled = canAddMore,
                                                                    modifier = Modifier
                                                                        .size(30.dp)
                                                                        .clip(CircleShape)
                                                                        .background(
                                                                            if (canAddMore) SeallPrimary.copy(alpha = 0.2f)
                                                                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                                                        )
                                                                ) {
                                                                    Icon(
                                                                        imageVector = Icons.Default.Add,
                                                                        contentDescription = "Increase",
                                                                        tint = if (canAddMore) SeallPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                                                        modifier = Modifier.size(15.dp)
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                } // close stocks.forEach Column
                                                    } // close Card
                                                } // close Dialog
                                            } // close if isStockDropdownExpanded
                                        }

                                        // Selected Items List (only shows items with quantity > 0)
                                        if (selectedOrderItems.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(14.dp))
                                            Text(
                                                text = "Selected Items (${selectedOrderItems.size}):",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))

                                            Column(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                selectedOrderItems.forEach { orderItem ->
                                                    val stockId = orderItem.stockId ?: return@forEach
                                                    val currentQty = itemQuantities[stockId] ?: 0
                                                    val matchingStock = stocks.find { it.id == stockId }
                                                    val maxAvailable = matchingStock?.quantity ?: Int.MAX_VALUE
                                                    val canAddMore = currentQty < maxAvailable
                                                    val subtotal = orderItem.price * currentQty

                                                    Surface(
                                                        shape = RoundedCornerShape(12.dp),
                                                        color = SeallPrimary.copy(alpha = 0.10f),
                                                        border = androidx.compose.foundation.BorderStroke(1.dp, SeallPrimary.copy(alpha = 0.4f)),
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Column(modifier = Modifier.weight(1f)) {
                                                                Text(
                                                                    text = orderItem.name,
                                                                    style = MaterialTheme.typography.bodyMedium,
                                                                    fontWeight = FontWeight.SemiBold,
                                                                    color = MaterialTheme.colorScheme.onSurface
                                                                )
                                                                Text(
                                                                    text = if (matchingStock != null && currentQty >= maxAvailable) {
                                                                        String.format(Locale.US, "₱%.2f × %d = ₱%.2f • Max stock (%d pcs)", orderItem.price, currentQty, subtotal, maxAvailable)
                                                                    } else {
                                                                        String.format(Locale.US, "₱%.2f × %d = ₱%.2f", orderItem.price, currentQty, subtotal)
                                                                    },
                                                                    style = MaterialTheme.typography.bodySmall,
                                                                    color = if (matchingStock != null && currentQty >= maxAvailable) SeallUnpaidAmber else MaterialTheme.colorScheme.onSurfaceVariant,
                                                                    fontWeight = if (matchingStock != null && currentQty >= maxAvailable) FontWeight.SemiBold else FontWeight.Normal
                                                                )
                                                            }

                                                            Row(
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                            ) {
                                                                // Decrement button
                                                                IconButton(
                                                                    onClick = {
                                                                        if (currentQty > 0) {
                                                                            val newQty = currentQty - 1
                                                                            if (newQty <= 0) {
                                                                                itemQuantities.remove(stockId)
                                                                            } else {
                                                                                itemQuantities[stockId] = newQty
                                                                            }
                                                                            val updatedItems = itemQuantities.filter { it.value > 0 }.mapNotNull { (sId, q) ->
                                                                                stocks.find { it.id == sId }?.let { s -> OrderItem(s.id, s.name, s.price, q) }
                                                                            }
                                                                            if (updatedItems.isNotEmpty()) {
                                                                                val sum = OrderItem.totalPrice(updatedItems)
                                                                                priceInput = if (sum % 1.0 == 0.0) sum.toLong().toString() else String.format(Locale.US, "%.2f", sum)
                                                                            }
                                                                            priceError = false
                                                                        }
                                                                    },
                                                                    modifier = Modifier
                                                                        .size(32.dp)
                                                                        .clip(CircleShape)
                                                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                                                ) {
                                                                    Icon(
                                                                        imageVector = Icons.Default.Remove,
                                                                        contentDescription = "Decrease",
                                                                        tint = MaterialTheme.colorScheme.onSurface,
                                                                        modifier = Modifier.size(16.dp)
                                                                    )
                                                                }

                                                                Text(
                                                                    text = "$currentQty",
                                                                    style = MaterialTheme.typography.titleMedium,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = SeallPrimary,
                                                                    modifier = Modifier.width(24.dp),
                                                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                                                )

                                                                // Increment button (disabled if max stock reached)
                                                                IconButton(
                                                                    onClick = {
                                                                        if (canAddMore) {
                                                                            val newQty = currentQty + 1
                                                                            itemQuantities[stockId] = newQty
                                                                            val updatedItems = itemQuantities.filter { it.value > 0 }.mapNotNull { (sId, q) ->
                                                                                stocks.find { it.id == sId }?.let { s -> OrderItem(s.id, s.name, s.price, q) }
                                                                            }
                                                                            val sum = OrderItem.totalPrice(updatedItems)
                                                                            priceInput = if (sum % 1.0 == 0.0) sum.toLong().toString() else String.format(Locale.US, "%.2f", sum)
                                                                            priceError = false
                                                                        }
                                                                    },
                                                                    enabled = canAddMore,
                                                                    modifier = Modifier
                                                                        .size(32.dp)
                                                                        .clip(CircleShape)
                                                                        .background(
                                                                            if (canAddMore) SeallPrimary.copy(alpha = 0.2f)
                                                                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                                                        )
                                                                ) {
                                                                    Icon(
                                                                        imageVector = Icons.Default.Add,
                                                                        contentDescription = "Increase",
                                                                        tint = if (canAddMore) SeallPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                                                        modifier = Modifier.size(16.dp)
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        if (computedItemsSummary.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Surface(
                                                shape = RoundedCornerShape(10.dp),
                                                color = SeallPrimary.copy(alpha = 0.15f),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.ShoppingCart,
                                                        contentDescription = null,
                                                        tint = SeallPrimary,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Text(
                                                        text = "$computedItemsSummary ($computedTotalItemCount items)",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Manual or auto-calculated price entry
                                    OutlinedTextField(
                                        value = priceInput,
                                        onValueChange = { input ->
                                            val filtered = input.filter { it.isDigit() || it == '.' }
                                            if (filtered.count { it == '.' } <= 1) {
                                                priceInput = filtered
                                                priceError = false
                                            }
                                        },
                                        label = { Text("Total Price in ₱") },
                                        placeholder = { Text("0.00") },
                                        isError = priceError,
                                        supportingText = if (priceError) {
                                            { Text("Enter a valid price greater than 0", color = MaterialTheme.colorScheme.error) }
                                        } else if (computedItemsSummary.isNotBlank()) {
                                            { Text("Auto-calculated from items (you can adjust if needed)", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                                        } else null,
                                        singleLine = true,
                                        leadingIcon = {
                                            Text(
                                                text = "₱",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = SeallPrimary,
                                                modifier = Modifier.padding(start = 12.dp)
                                            )
                                        },
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Decimal,
                                            imeAction = ImeAction.Next
                                        ),
                                        keyboardActions = KeyboardActions(
                                            onNext = {
                                                val p = priceInput.toDoubleOrNull()
                                                if (p != null && p > 0.0) {
                                                    currentStep = 3
                                                } else {
                                                    priceError = true
                                                }
                                            }
                                        ),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = SeallPrimary,
                                            focusedLabelColor = SeallPrimary
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .focusRequester(priceFocusRequester)
                                    )

                                    LaunchedEffect(Unit) {
                                        priceFocusRequester.requestFocus()
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))

                                    Button(
                                        onClick = {
                                            val p = priceInput.toDoubleOrNull()
                                            if (p != null && p > 0.0) {
                                                currentStep = 3
                                            } else {
                                                priceError = true
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = SeallPrimary,
                                            contentColor = SeallDarkContrast
                                        )
                                    ) {
                                        Text(
                                            text = "Next: Payment Status",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                                    }
                                }
                            }

                            3 -> {
                                // --- STEP 3: "Paid?: Yes or No" Instant Commit ---
                                val parsedPrice = priceInput.toDoubleOrNull() ?: 0.0
                                val itemsJson = OrderItem.listToJson(selectedOrderItems)
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .verticalScroll(rememberScrollState()),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Is this order already paid?",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "$customerName · " + String.format(Locale.US, "₱%.2f", parsedPrice),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    if (computedItemsSummary.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                        ) {
                                            Text(
                                                text = "$computedItemsSummary • $computedTotalItemCount items",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))

                                    // Large one-handed action buttons
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                                    ) {
                                        // YES Button -> Immediate save & close
                                        Button(
                                            onClick = {
                                                onSubmit(
                                                    customerName,
                                                    parsedPrice,
                                                    true,
                                                    computedItemsSummary,
                                                    itemsJson,
                                                    computedTotalItemCount
                                                )
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(52.dp),
                                            shape = RoundedCornerShape(14.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = SeallPaidGreen,
                                                contentColor = Color.White
                                            )
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "YES",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 0.5.sp
                                            )
                                        }

                                        // NO Button -> Immediate save & close
                                        Button(
                                            onClick = {
                                                onSubmit(
                                                    customerName,
                                                    parsedPrice,
                                                    false,
                                                    computedItemsSummary,
                                                    itemsJson,
                                                    computedTotalItemCount
                                                )
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(52.dp),
                                            shape = RoundedCornerShape(14.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = SeallUnpaidAmber,
                                                contentColor = Color.White
                                            )
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(20.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "NO",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 0.5.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
