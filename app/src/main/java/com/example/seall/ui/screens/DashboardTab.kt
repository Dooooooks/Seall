package com.example.seall.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seall.data.model.Ingredient
import com.example.seall.data.model.Order
import com.example.seall.data.model.StockIngredient
import com.example.seall.data.model.StockItem
import com.example.seall.ui.theme.SeallDarkContrast
import com.example.seall.ui.theme.SeallPaidGreen
import com.example.seall.ui.theme.SeallPaidGreenContainer
import com.example.seall.ui.theme.SeallPrimary
import com.example.seall.ui.theme.SeallUnpaidAmber
import com.example.seall.ui.theme.SeallUnpaidAmberContainer
import com.example.seall.util.CsvHelper
import com.example.seall.util.DateUtils
import com.example.seall.util.IngredientCostHelper
import com.example.seall.util.IngredientUsageSummary
import com.example.seall.util.ProductRecipeSummary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class DashboardPeriod {
    TODAY, LIFETIME
}

data class ClientSettlement(
    val customerName: String,
    val totalAmount: Double,
    val orderCount: Int,
    val orders: List<Order>,
    val latestTimestamp: Long
)

@Composable
fun DashboardTab(
    allOrders: List<Order>,
    allIngredients: List<Ingredient> = emptyList(),
    stocks: List<StockItem> = emptyList(),
    stockIngredients: List<StockIngredient> = emptyList(),
    deductIngredients: Boolean = true,
    onToggleDeductIngredients: (Boolean) -> Unit = {},
    onMarkAsPaid: (Order) -> Unit,
    onImportOrders: (List<Order>) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedPeriod by remember { mutableStateOf(DashboardPeriod.TODAY) }
    var pendingImportOrders by remember { mutableStateOf<List<Order>?>(null) }

    // Launcher for exporting CSV file
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    val csvString = CsvHelper.generateOrdersCsv(allOrders)
                    outputStream.write(csvString.toByteArray(Charsets.UTF_8))
                }
                Toast.makeText(context, "Exported ${allOrders.size} orders to CSV successfully!", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Launcher for importing CSV file
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                val csvContent = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() } ?: ""
                val parsed = CsvHelper.parseOrdersCsv(csvContent)
                if (parsed.isEmpty()) {
                    Toast.makeText(context, "No valid orders found in selected CSV.", Toast.LENGTH_LONG).show()
                } else {
                    pendingImportOrders = parsed
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to read CSV: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    val startOfToday = remember { DateUtils.getStartOfDay() }
    val endOfToday = remember { DateUtils.getEndOfDay() }

    val displayOrders = remember(allOrders, selectedPeriod) {
        if (selectedPeriod == DashboardPeriod.TODAY) {
            allOrders.filter { it.createdAt in startOfToday..endOfToday }
        } else {
            allOrders
        }
    }

    val paidTotal = remember(displayOrders) {
        displayOrders.filter { it.isPaid }.sumOf { it.price }
    }
    val unpaidTotal = remember(displayOrders) {
        displayOrders.filter { !it.isPaid }.sumOf { it.price }
    }
    val combinedTotal = remember(displayOrders) {
        displayOrders.sumOf { it.price }
    }
    val unpaidOrders = remember(displayOrders) {
        displayOrders.filter { !it.isPaid }
    }

    val displayGeneralIngredients = remember(allIngredients, selectedPeriod) {
        if (selectedPeriod == DashboardPeriod.TODAY) {
            allIngredients.filter { it.createdAt in startOfToday..endOfToday }
        } else {
            allIngredients
        }
    }

    val totalItemsSold = remember(displayOrders) {
        displayOrders.sumOf { it.totalItemCount }
    }

    val ordersIngredientsCost = remember(displayOrders, stocks, stockIngredients) {
        IngredientCostHelper.calculateTotalOrdersIngredientsCost(displayOrders, stocks, stockIngredients)
    }

    val generalIngredientsCost = remember(displayGeneralIngredients) {
        displayGeneralIngredients.sumOf { it.price }
    }

    val totalIngredientsCost = remember(ordersIngredientsCost, generalIngredientsCost) {
        ordersIngredientsCost + generalIngredientsCost
    }

    val totalEarnings = remember(combinedTotal, totalIngredientsCost) {
        combinedTotal - totalIngredientsCost
    }

    val ingredientUsages = remember(displayOrders, stocks, stockIngredients) {
        IngredientCostHelper.computeIngredientUsages(displayOrders, stocks, stockIngredients)
    }

    val productRecipeSummaries = remember(stocks, stockIngredients) {
        IngredientCostHelper.computeProductRecipeSummaries(stocks, stockIngredients)
    }

    // Group pending settlement by customer name (combining totals)
    val clientSettlements = remember(unpaidOrders) {
        unpaidOrders
            .groupBy { it.customerName.trim().lowercase(Locale.getDefault()) }
            .map { (_, clientOrders) ->
                ClientSettlement(
                    customerName = clientOrders.first().customerName.trim(),
                    totalAmount = clientOrders.sumOf { it.price },
                    orderCount = clientOrders.size,
                    orders = clientOrders,
                    latestTimestamp = clientOrders.maxOf { it.createdAt }
                )
            }
            .sortedByDescending { it.latestTimestamp }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Sales Analytics",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = if (selectedPeriod == DashboardPeriod.TODAY) {
                    "Showing today's sales (${DateUtils.formatShortDate(System.currentTimeMillis())})"
                } else {
                    "Aggregated totals across all recorded sales"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Today vs Lifetime Toggle
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                DashboardPeriod.values().forEach { period ->
                    val isSelected = selectedPeriod == period
                    val label = when (period) {
                        DashboardPeriod.TODAY -> "Today"
                        DashboardPeriod.LIFETIME -> "Lifetime"
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(9.dp))
                            .background(
                                if (isSelected) SeallPrimary else Color.Transparent
                            )
                            .clickable { selectedPeriod = period }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) SeallDarkContrast else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Revenue Deduction Toggle: Subtract Ingredients from Revenue
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleDeductIngredients(!deductIngredients) }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    if (deductIngredients) SeallPaidGreenContainer else MaterialTheme.colorScheme.surfaceVariant
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (deductIngredients) Icons.Default.Savings else Icons.Default.AttachMoney,
                                contentDescription = null,
                                tint = if (deductIngredients) SeallPaidGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Subtract Ingredients Cost",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (deductIngredients)
                                    "Displaying Net Revenue (Gross − Ingredients)"
                                else
                                    "Displaying Gross Revenue (before ingredients)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Switch(
                        checked = deductIngredients,
                        onCheckedChange = onToggleDeductIngredients,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = SeallDarkContrast,
                            checkedTrackColor = SeallPrimary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }
        }

        // Metrics Grid / Row
        item {
            val periodLabel = if (selectedPeriod == DashboardPeriod.TODAY) "Today's" else "Lifetime"
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Primary Hero Card: Net or Gross depending on toggle
                if (deductIngredients) {
                    AnalyticsHeroCard(
                        title = "$periodLabel Total Earnings (Net)",
                        value = String.format(Locale.US, "₱%.2f", totalEarnings),
                        icon = Icons.Default.Savings,
                        accentColor = if (totalEarnings >= 0) SeallPaidGreen else MaterialTheme.colorScheme.error,
                        subtitle = "Gross ₱${String.format(Locale.US, "%.2f", combinedTotal)} ($totalItemsSold items) − Ingredients ₱${String.format(Locale.US, "%.2f", totalIngredientsCost)}"
                    )
                } else {
                    AnalyticsHeroCard(
                        title = "$periodLabel Gross Revenue",
                        value = String.format(Locale.US, "₱%.2f", combinedTotal),
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        accentColor = SeallPrimary,
                        subtitle = "Full sales revenue before deductions ($totalItemsSold items) • Ingredients cost: ₱${String.format(Locale.US, "%.2f", totalIngredientsCost)}"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (deductIngredients) {
                        AnalyticsMiniCard(
                            title = "Gross Sales",
                            value = String.format(Locale.US, "₱%.2f", combinedTotal),
                            icon = Icons.AutoMirrored.Filled.TrendingUp,
                            accentColor = SeallPrimary,
                            containerColor = SeallPrimary.copy(alpha = 0.2f),
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        AnalyticsMiniCard(
                            title = "Net Revenue",
                            value = String.format(Locale.US, "₱%.2f", totalEarnings),
                            icon = Icons.Default.Savings,
                            accentColor = if (totalEarnings >= 0) SeallPaidGreen else MaterialTheme.colorScheme.error,
                            containerColor = if (totalEarnings >= 0) SeallPaidGreenContainer else MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    AnalyticsMiniCard(
                        title = "Ingredients Cost",
                        value = String.format(Locale.US, "₱%.2f", totalIngredientsCost),
                        icon = Icons.Default.Egg,
                        accentColor = SeallUnpaidAmber,
                        containerColor = SeallUnpaidAmberContainer,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AnalyticsMiniCard(
                        title = "Paid Total",
                        value = String.format(Locale.US, "₱%.2f", paidTotal),
                        icon = Icons.Default.AttachMoney,
                        accentColor = SeallPaidGreen,
                        containerColor = SeallPaidGreenContainer,
                        modifier = Modifier.weight(1f)
                    )

                    AnalyticsMiniCard(
                        title = "Unpaid Total",
                        value = String.format(Locale.US, "₱%.2f", unpaidTotal),
                        icon = Icons.Default.HourglassEmpty,
                        accentColor = SeallUnpaidAmber,
                        containerColor = SeallUnpaidAmberContainer,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Ingredients Breakdown Section
        item {
            IngredientsBreakdownCard(
                period = selectedPeriod,
                ingredientUsages = ingredientUsages,
                totalIngredientsCost = totalIngredientsCost,
                productRecipeSummaries = productRecipeSummaries,
                hasStockIngredients = stockIngredients.isNotEmpty()
            )
        }



        // Unpaid Debtors Section
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.People,
                    contentDescription = null,
                    tint = SeallUnpaidAmber,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Pending Settlement (${clientSettlements.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        if (clientSettlements.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(SeallPaidGreenContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircleOutline,
                                contentDescription = null,
                                tint = SeallPaidGreen,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "All Orders Settled!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "There are currently no outstanding unpaid tabs.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        } else {
            items(clientSettlements, key = { it.customerName.lowercase(Locale.getDefault()) }) { settlement ->
                DebtorRowCard(
                    settlement = settlement,
                    onSettle = {
                        settlement.orders.forEach { onMarkAsPaid(it) }
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Import Confirmation Dialog
    pendingImportOrders?.let { ordersToImport ->
        AlertDialog(
            onDismissRequest = { pendingImportOrders = null },
            title = { Text("Import CSV Orders", fontWeight = FontWeight.Bold) },
            text = {
                Text("Found ${ordersToImport.size} transactions in CSV file. Do you want to import them into Seall?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onImportOrders(ordersToImport)
                        Toast.makeText(context, "Successfully imported ${ordersToImport.size} orders!", Toast.LENGTH_LONG).show()
                        pendingImportOrders = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SeallPrimary,
                        contentColor = SeallDarkContrast
                    )
                ) {
                    Text("Import ${ordersToImport.size} Orders", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingImportOrders = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun AnalyticsHeroCard(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    subtitle: String? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun AnalyticsMiniCard(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(115.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(containerColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
        }
    }
}

@Composable
private fun DebtorRowCard(
    settlement: ClientSettlement,
    onSettle: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
    val timeFormatted = dateFormat.format(Date(settlement.latestTimestamp))
    val totalItemsCount = remember(settlement) { settlement.orders.sumOf { it.totalItemCount } }
    val itemsOrderedSummary = remember(settlement) {
        settlement.orders.mapNotNull { it.itemsSummary.takeIf { s -> s.isNotBlank() } }.joinToString(", ")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = settlement.customerName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )

                if (itemsOrderedSummary.isNotBlank()) {
                    Text(
                        text = "$totalItemsCount items • $itemsOrderedSummary",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = if (settlement.orderCount > 1) {
                        "${settlement.orderCount} pending orders • Latest $timeFormatted"
                    } else {
                        timeFormatted
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    fontSize = 10.sp
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = String.format(Locale.US, "₱%.2f", settlement.totalAmount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SeallUnpaidAmber
                )

                Button(
                    onClick = onSettle,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SeallPaidGreen,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Settle", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun IngredientsBreakdownCard(
    period: DashboardPeriod,
    ingredientUsages: List<IngredientUsageSummary>,
    totalIngredientsCost: Double,
    productRecipeSummaries: List<ProductRecipeSummary>,
    hasStockIngredients: Boolean
) {
    var isExpanded by remember { mutableStateOf(false) }
    var activeSubTab by remember { mutableStateOf(0) } // 0 = Used in sales, 1 = Product Recipes

    val periodLabel = if (period == DashboardPeriod.TODAY) "Today" else "Lifetime"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(SeallUnpaidAmberContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Egg,
                            contentDescription = null,
                            tint = SeallUnpaidAmber,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Ingredients Breakdown",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (totalIngredientsCost > 0.0) {
                                "${ingredientUsages.size} ingredients used • ${String.format(Locale.US, "₱%.2f", totalIngredientsCost)}"
                            } else if (hasStockIngredients) {
                                "${productRecipeSummaries.count { it.ingredients.isNotEmpty() }} products with recipes configured"
                            } else {
                                "Configure recipes in Stocks tab"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (totalIngredientsCost > 0.0) {
                        Text(
                            text = String.format(Locale.US, "₱%.2f", totalIngredientsCost),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = SeallUnpaidAmber
                        )
                    }
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Sub-tab toggles if there are recipes
                    if (productRecipeSummaries.any { it.ingredients.isNotEmpty() }) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(3.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val tabs = listOf("Used in Sales ($periodLabel)", "Product Recipes")
                            tabs.forEachIndexed { index, label ->
                                val isSelected = activeSubTab == index
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) SeallPrimary else Color.Transparent)
                                        .clickable { activeSubTab = index }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) SeallDarkContrast else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    if (activeSubTab == 0) {
                        if (ingredientUsages.isEmpty()) {
                            Text(
                                text = "No ingredients used in sales for $periodLabel yet. When orders with recipes are placed, ingredients and costs will automatically show up here.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        } else {
                            ingredientUsages.forEach { usage ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surface,
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
                                                text = usage.ingredientName,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "${usage.stockNames.joinToString(", ")} • ${usage.quantityDescriptions.joinToString(", ")}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 11.sp
                                            )
                                        }
                                        Text(
                                            text = String.format(Locale.US, "₱%.2f", usage.totalCost),
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = SeallUnpaidAmber
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Product Recipes tab
                        val withRecipes = productRecipeSummaries.filter { it.ingredients.isNotEmpty() }
                        if (withRecipes.isEmpty()) {
                            Text(
                                text = "No products have recipe ingredients configured yet. Add them in the Stocks tab.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            withRecipes.forEach { summary ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = summary.stock.name,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "Cost: ${String.format(Locale.US, "₱%.2f", summary.totalRecipeCost)} / unit",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.SemiBold,
                                                color = SeallUnpaidAmber
                                            )
                                        }
                                        Text(
                                            text = summary.ingredients.joinToString(", ") { "${it.name} (${it.quantity}, ₱${String.format(Locale.US, "%.2f", it.cost)})" },
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 11.sp
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Price: ₱${String.format(Locale.US, "%.2f", summary.stock.price)}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = "Profit margin: ₱${String.format(Locale.US, "%.2f", summary.profitPerUnit)}",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = if (summary.profitPerUnit >= 0) SeallPaidGreen else MaterialTheme.colorScheme.error
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


