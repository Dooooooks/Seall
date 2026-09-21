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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.material3.Text
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
import com.example.seall.data.model.Order
import com.example.seall.ui.theme.SeallDarkContrast
import com.example.seall.ui.theme.SeallPaidGreen
import com.example.seall.ui.theme.SeallPrimary
import com.example.seall.ui.theme.SeallUnpaidAmber
import java.util.Locale

@Composable
fun RapidEntryWizardModal(
    isOpen: Boolean,
    editingOrder: Order? = null,
    onDismiss: () -> Unit,
    onSubmit: (name: String, price: Double, isPaid: Boolean) -> Unit
) {
    if (!isOpen) return

    var currentStep by remember(editingOrder) { mutableIntStateOf(1) }
    var customerName by remember(editingOrder) { mutableStateOf(editingOrder?.customerName ?: "") }
    var priceInput by remember(editingOrder) {
        mutableStateOf(editingOrder?.let { String.format(Locale.US, "%.2f", it.price) } ?: "")
    }
    var nameError by remember { mutableStateOf(false) }
    var priceError by remember { mutableStateOf(false) }

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
                .padding(horizontal = 24.dp),
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
                                Column(modifier = Modifier.fillMaxWidth()) {
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
                                        placeholder = { Text("e.g. Maria, Juan D.") },
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
                                            text = "Next: Enter Price",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                                    }
                                }
                            }

                            2 -> {
                                // --- STEP 2: Price Entry ---
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "Order Amount (₱)",
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

                                    Spacer(modifier = Modifier.height(16.dp))

                                    OutlinedTextField(
                                        value = priceInput,
                                        onValueChange = { input ->
                                            val filtered = input.filter { it.isDigit() || it == '.' }
                                            if (filtered.count { it == '.' } <= 1) {
                                                priceInput = filtered
                                                priceError = false
                                            }
                                        },
                                        label = { Text("Price in ₱") },
                                        placeholder = { Text("0.00") },
                                        isError = priceError,
                                        supportingText = if (priceError) {
                                            { Text("Enter a valid price greater than 0", color = MaterialTheme.colorScheme.error) }
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
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
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

                                    Spacer(modifier = Modifier.height(24.dp))

                                    // Large one-handed action buttons
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                                    ) {
                                        // YES Button -> Immediate save & close
                                        Button(
                                            onClick = {
                                                onSubmit(customerName, parsedPrice, true)
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
                                                onSubmit(customerName, parsedPrice, false)
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
