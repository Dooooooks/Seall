package com.example.seall.ui.screens

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.seall.data.model.StockIngredient
import com.example.seall.data.model.StockItem
import com.example.seall.ui.theme.SeallDarkContrast
import com.example.seall.ui.theme.SeallPaidGreen
import com.example.seall.ui.theme.SeallPaidGreenContainer
import com.example.seall.ui.theme.SeallPrimary
import com.example.seall.ui.theme.SeallUnpaidAmber
import com.example.seall.ui.theme.SeallUnpaidAmberContainer
import java.util.Locale

@Composable
fun StocksTab(
    stocks: List<StockItem>,
    stockIngredients: List<StockIngredient> = emptyList(),
    onAddStock: (name: String, price: Double, quantity: Int) -> Unit,
    onUpdateStock: (stock: StockItem, name: String, price: Double, quantity: Int) -> Unit,
    onAdjustStockQuantity: (stock: StockItem, delta: Int) -> Unit = { _, _ -> },
    onSetStockQuantity: (stock: StockItem, quantity: Int) -> Unit = { _, _ -> },
    onDeleteStock: (stock: StockItem) -> Unit,
    onAddStockIngredient: (stockId: Long, name: String, quantity: String, cost: Double) -> Unit = { _, _, _, _ -> },
    onUpdateStockIngredient: (stockIngredient: StockIngredient, name: String, quantity: String, cost: Double) -> Unit = { _, _, _, _ -> },
    onDeleteStockIngredient: (stockIngredient: StockIngredient) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    var isAddStockDialogOpen by remember { mutableStateOf(false) }
    var stockToEdit by remember { mutableStateOf<StockItem?>(null) }
    var stockToDelete by remember { mutableStateOf<StockItem?>(null) }
    var stockForDirectQuantityEdit by remember { mutableStateOf<StockItem?>(null) }

    var stockForAddingIngredient by remember { mutableStateOf<StockItem?>(null) }
    var stockIngredientToEdit by remember { mutableStateOf<StockIngredient?>(null) }
    var stockIngredientToDelete by remember { mutableStateOf<StockIngredient?>(null) }

    val filteredStocks = remember(stocks, searchQuery) {
        if (searchQuery.isBlank()) {
            stocks
        } else {
            stocks.filter { it.name.contains(searchQuery.trim(), ignoreCase = true) }
        }
    }

    val totalAvailableStock = remember(stocks) {
        stocks.sumOf { it.quantity }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Info & Overview Card
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Products & Stocks",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Track available product quantities, default prices, and recipes",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Quick Inventory Stats Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Total Inventory in Stock",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$totalAvailableStock pcs",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "${stocks.size} products",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Search Bar for Stocks
            if (stocks.isNotEmpty() || searchQuery.isNotBlank()) {
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search products (e.g. Brownies)") },
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = SeallPrimary
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SeallPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Stock Count Header
            if (stocks.isNotEmpty()) {
                item {
                    Text(
                        text = "PRODUCTS (${filteredStocks.size})",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            if (filteredStocks.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Inventory2,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = if (searchQuery.isNotBlank()) "No products found" else "No products added yet",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (searchQuery.isNotBlank()) {
                                    "Try a different search keyword"
                                } else {
                                    "Add products you sell with their default price and stock count. You can also add their recipe ingredients list!"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            if (searchQuery.isBlank()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { isAddStockDialogOpen = true },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = SeallPrimary,
                                        contentColor = SeallDarkContrast
                                    )
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Add First Product", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            } else {
                items(filteredStocks, key = { it.id }) { stock ->
                    val itemIngredients = stockIngredients.filter { it.stockId == stock.id }
                    StockCard(
                        stock = stock,
                        ingredients = itemIngredients,
                        onAdjustQuantity = { delta -> onAdjustStockQuantity(stock, delta) },
                        onEditQuantity = { stockForDirectQuantityEdit = stock },
                        onAddIngredient = { stockForAddingIngredient = stock },
                        onEditIngredient = { stockIngredientToEdit = it },
                        onDeleteIngredient = { stockIngredientToDelete = it },
                        onEdit = { stockToEdit = stock },
                        onDelete = { stockToDelete = stock }
                    )
                }
            }
        }

        // Floating Action Button to Add Product
        FloatingActionButton(
            onClick = { isAddStockDialogOpen = true },
            containerColor = SeallPrimary,
            contentColor = SeallDarkContrast,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Product",
                modifier = Modifier.size(24.dp)
            )
        }
    }

    // Add Product Dialog
    if (isAddStockDialogOpen) {
        StockFormDialog(
            title = "Add Product",
            initialName = "",
            initialPrice = "",
            initialQuantity = "0",
            confirmText = "Add",
            onDismiss = { isAddStockDialogOpen = false },
            onConfirm = { name, price, quantity ->
                onAddStock(name, price, quantity)
                isAddStockDialogOpen = false
            }
        )
    }

    // Edit Product Dialog
    stockToEdit?.let { stock ->
        StockFormDialog(
            title = "Edit Product",
            initialName = stock.name,
            initialPrice = if (stock.price % 1.0 == 0.0) stock.price.toLong().toString() else String.format(Locale.US, "%.2f", stock.price),
            initialQuantity = stock.quantity.toString(),
            confirmText = "Save",
            onDismiss = { stockToEdit = null },
            onConfirm = { name, price, quantity ->
                onUpdateStock(stock, name, price, quantity)
                stockToEdit = null
            }
        )
    }

    // Direct Quick Stock Quantity Dialog
    stockForDirectQuantityEdit?.let { stock ->
        QuickStockQuantityDialog(
            stock = stock,
            onDismiss = { stockForDirectQuantityEdit = null },
            onConfirm = { newQuantity ->
                onSetStockQuantity(stock, newQuantity)
                stockForDirectQuantityEdit = null
            }
        )
    }

    // Delete Stock Confirmation Dialog
    stockToDelete?.let { stock ->
        AlertDialog(
            onDismissRequest = { stockToDelete = null },
            title = { Text("Delete Product", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove \"${stock.name}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteStock(stock)
                        stockToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { stockToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add Stock Ingredient Dialog
    stockForAddingIngredient?.let { stock ->
        StockIngredientFormDialog(
            title = "Add Ingredient to ${stock.name}",
            initialName = "",
            initialQuantity = "",
            initialCost = "",
            confirmText = "Add",
            onDismiss = { stockForAddingIngredient = null },
            onConfirm = { name, quantity, cost ->
                onAddStockIngredient(stock.id, name, quantity, cost)
                stockForAddingIngredient = null
            }
        )
    }

    // Edit Stock Ingredient Dialog
    stockIngredientToEdit?.let { ingredient ->
        val parentStock = stocks.find { it.id == ingredient.stockId }
        val stockName = parentStock?.name ?: "Stock"
        StockIngredientFormDialog(
            title = "Edit Ingredient for $stockName",
            initialName = ingredient.name,
            initialQuantity = ingredient.quantity,
            initialCost = if (ingredient.cost > 0.0) {
                if (ingredient.cost % 1.0 == 0.0) ingredient.cost.toLong().toString() else String.format(Locale.US, "%.2f", ingredient.cost)
            } else "",
            confirmText = "Save",
            onDismiss = { stockIngredientToEdit = null },
            onConfirm = { name, quantity, cost ->
                onUpdateStockIngredient(ingredient, name, quantity, cost)
                stockIngredientToEdit = null
            }
        )
    }

    // Delete Stock Ingredient Confirmation Dialog
    stockIngredientToDelete?.let { ingredient ->
        AlertDialog(
            onDismissRequest = { stockIngredientToDelete = null },
            title = { Text("Delete Ingredient", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove \"${ingredient.name}\" from this recipe?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteStockIngredient(ingredient)
                        stockIngredientToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { stockIngredientToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun StockCard(
    stock: StockItem,
    ingredients: List<StockIngredient> = emptyList(),
    onAdjustQuantity: (delta: Int) -> Unit = {},
    onEditQuantity: () -> Unit = {},
    onAddIngredient: () -> Unit = {},
    onEditIngredient: (StockIngredient) -> Unit = {},
    onDeleteIngredient: (StockIngredient) -> Unit = {},
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Main Product Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 12.dp, top = 14.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SeallPrimary.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = null,
                            tint = SeallPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = stock.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Default Price",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Price Tag
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Text(
                            text = String.format(Locale.US, "₱%.2f", stock.price),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }

                    // Edit Button
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Stock",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Delete Button
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete Stock",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Editable Stock Quantity Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Clickable Stock Pill
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (stock.quantity > 0) SeallPaidGreenContainer else SeallUnpaidAmberContainer,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onEditQuantity() }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = if (stock.quantity > 0) "● In Stock: ${stock.quantity} pcs" else "⚠ Out of Stock (0 pcs)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (stock.quantity > 0) SeallPaidGreen else SeallUnpaidAmber
                        )
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit quantity",
                            tint = (if (stock.quantity > 0) SeallPaidGreen else SeallUnpaidAmber).copy(alpha = 0.7f),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                // Inline quick [-] and [+] quantity adjusters
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Decrement Stock
                    IconButton(
                        onClick = { onAdjustQuantity(-1) },
                        enabled = stock.quantity > 0,
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(if (stock.quantity > 0) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease Stock",
                            tint = if (stock.quantity > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    Text(
                        text = "${stock.quantity}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.width(32.dp),
                        textAlign = TextAlign.Center
                    )

                    // Increment Stock
                    IconButton(
                        onClick = { onAdjustQuantity(1) },
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(SeallPrimary.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase Stock",
                            tint = SeallPrimary,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Expandable Recipe / Ingredients Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                    .clickable { isExpanded = !isExpanded }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Egg,
                            contentDescription = null,
                            tint = SeallPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Ingredients (${ingredients.size})",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SeallPrimary.copy(alpha = 0.15f),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { onAddIngredient() }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = SeallPrimary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "Add",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SeallPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                            contentDescription = if (isExpanded) "Collapse" else "Expand",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Expanded ingredients details
            if (isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.18f))
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (ingredients.isEmpty()) {
                        Text(
                            text = "No ingredients listed yet for ${stock.name}. Tap '+ Add' to specify ingredients (e.g. 3 Eggs, 250g Flour).",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    } else {
                        ingredients.forEach { ingredient ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = ingredient.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            modifier = Modifier.padding(top = 2.dp)
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                                            ) {
                                                Text(
                                                    text = ingredient.quantity.ifBlank { "1 pc" },
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                                )
                                            }

                                            if (ingredient.cost > 0.0) {
                                                Text(
                                                    text = String.format(Locale.US, "₱%.2f", ingredient.cost),
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                        IconButton(
                                            onClick = { onEditIngredient(ingredient) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Edit",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }
                                        IconButton(
                                            onClick = { onDeleteIngredient(ingredient) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.DeleteOutline,
                                                contentDescription = "Delete",
                                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    TextButton(
                        onClick = onAddIngredient,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = SeallPrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Add Ingredient to ${stock.name}",
                            color = SeallPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickStockQuantityDialog(
    stock: StockItem,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var quantityInput by remember { mutableStateOf(stock.quantity.toString()) }
    val focusRequester = remember { FocusRequester() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            decorFitsSystemWindows = false,
            usePlatformDefaultWidth = false
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
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { /* prevent click through */ }
                    ),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(22.dp)
                ) {
                    Text(
                        text = "Update Stock",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "For: ${stock.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SeallPrimary,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = quantityInput,
                        onValueChange = { input ->
                            quantityInput = input.filter { it.isDigit() }
                        },
                        label = { Text("Available Stock Quantity (pcs)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                val q = quantityInput.toIntOrNull() ?: 0
                                onConfirm(maxOf(0, q))
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SeallPrimary,
                            focusedLabelColor = SeallPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                    )

                    LaunchedEffect(Unit) {
                        focusRequester.requestFocus()
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick restock chips (+5, +10, +25, +50)
                    Text(
                        text = "Quick Restock (+):",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(5, 10, 20, 50).forEach { boost ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        val current = quantityInput.toIntOrNull() ?: 0
                                        quantityInput = (current + boost).toString()
                                    }
                            ) {
                                Text(
                                    text = "+$boost",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val q = quantityInput.toIntOrNull() ?: 0
                                onConfirm(maxOf(0, q))
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SeallPrimary,
                                contentColor = SeallDarkContrast
                            )
                        ) {
                            Text("Save Stock", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StockFormDialog(
    title: String,
    initialName: String,
    initialPrice: String,
    initialQuantity: String = "0",
    confirmText: String,
    onDismiss: () -> Unit,
    onConfirm: (name: String, price: Double, quantity: Int) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var priceStr by remember { mutableStateOf(initialPrice) }
    var quantityStr by remember { mutableStateOf(initialQuantity) }
    var nameError by remember { mutableStateOf(false) }
    var priceError by remember { mutableStateOf(false) }

    val nameFocusRequester = remember { FocusRequester() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            decorFitsSystemWindows = false,
            usePlatformDefaultWidth = false
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
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { /* prevent click through */ }
                    ),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(22.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Name Field
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            if (it.isNotBlank()) nameError = false
                        },
                        label = { Text("Product Name (e.g. Brownies)") },
                        isError = nameError,
                        supportingText = if (nameError) {
                            { Text("Name is required", color = MaterialTheme.colorScheme.error) }
                        } else null,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
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

                    Spacer(modifier = Modifier.height(12.dp))

                    // Price Field
                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = { input ->
                            val filtered = input.filter { it.isDigit() || it == '.' }
                            if (filtered.count { it == '.' } <= 1) {
                                priceStr = filtered
                                priceError = false
                            }
                        },
                        label = { Text("Default Price in ₱") },
                        placeholder = { Text("0.00") },
                        leadingIcon = {
                            Text(
                                text = "₱",
                                fontWeight = FontWeight.Bold,
                                color = SeallPrimary,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        },
                        isError = priceError,
                        supportingText = if (priceError) {
                            { Text("Enter a valid price greater than 0", color = MaterialTheme.colorScheme.error) }
                        } else null,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SeallPrimary,
                            focusedLabelColor = SeallPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Stock Quantity Field
                    OutlinedTextField(
                        value = quantityStr,
                        onValueChange = { input ->
                            quantityStr = input.filter { it.isDigit() }
                        },
                        label = { Text("Available Stock Quantity (pcs)") },
                        placeholder = { Text("0") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                val p = priceStr.toDoubleOrNull()
                                val q = quantityStr.toIntOrNull() ?: 0
                                if (name.trim().isNotBlank() && p != null && p > 0.0) {
                                    onConfirm(name.trim(), p, maxOf(0, q))
                                } else {
                                    if (name.trim().isBlank()) nameError = true
                                    if (p == null || p <= 0.0) priceError = true
                                }
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SeallPrimary,
                            focusedLabelColor = SeallPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Dialog Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val p = priceStr.toDoubleOrNull()
                                val q = quantityStr.toIntOrNull() ?: 0
                                if (name.trim().isNotBlank() && p != null && p > 0.0) {
                                    onConfirm(name.trim(), p, maxOf(0, q))
                                } else {
                                    if (name.trim().isBlank()) nameError = true
                                    if (p == null || p <= 0.0) priceError = true
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SeallPrimary,
                                contentColor = SeallDarkContrast
                            )
                        ) {
                            Text(confirmText, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StockIngredientFormDialog(
    title: String,
    initialName: String,
    initialQuantity: String,
    initialCost: String,
    confirmText: String,
    onDismiss: () -> Unit,
    onConfirm: (name: String, quantity: String, cost: Double) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var quantity by remember { mutableStateOf(initialQuantity) }
    var costStr by remember { mutableStateOf(initialCost) }
    var nameError by remember { mutableStateOf(false) }

    val nameFocusRequester = remember { FocusRequester() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            decorFitsSystemWindows = false,
            usePlatformDefaultWidth = false
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
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { /* prevent click through */ }
                    ),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(22.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Name Field
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            if (it.isNotBlank()) nameError = false
                        },
                        label = { Text("Ingredient Name (e.g. Eggs, Flour)") },
                        isError = nameError,
                        supportingText = if (nameError) {
                            { Text("Name is required", color = MaterialTheme.colorScheme.error) }
                        } else null,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
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

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quantity Field (Number of items / amount)
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Number of Items / Quantity (e.g. 3 pcs, 250g)") },
                        placeholder = { Text("e.g. 3 pcs, 2 cups, 1") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.None,
                            imeAction = ImeAction.Next
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SeallPrimary,
                            focusedLabelColor = SeallPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Optional Cost Field
                    OutlinedTextField(
                        value = costStr,
                        onValueChange = { input ->
                            val filtered = input.filter { it.isDigit() || it == '.' }
                            if (filtered.count { it == '.' } <= 1) {
                                costStr = filtered
                            }
                        },
                        label = { Text("Cost in ₱ (Optional)") },
                        placeholder = { Text("0.00") },
                        leadingIcon = {
                            Text(
                                text = "₱",
                                fontWeight = FontWeight.Bold,
                                color = SeallPrimary,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (name.trim().isNotBlank()) {
                                    val cost = costStr.toDoubleOrNull() ?: 0.0
                                    onConfirm(name.trim(), quantity.trim().ifBlank { "1 pc" }, cost)
                                } else {
                                    nameError = true
                                }
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SeallPrimary,
                            focusedLabelColor = SeallPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (name.trim().isNotBlank()) {
                                    val cost = costStr.toDoubleOrNull() ?: 0.0
                                    onConfirm(name.trim(), quantity.trim().ifBlank { "1 pc" }, cost)
                                } else {
                                    nameError = true
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SeallPrimary,
                                contentColor = SeallDarkContrast
                            )
                        ) {
                            Text(confirmText, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
