package com.example.noboros.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.noboros.data.model.Account
import com.example.noboros.data.model.AccountCategory
import com.example.noboros.data.model.TransactionCategory
import com.example.noboros.data.model.TransactionType
import com.example.noboros.ui.theme.NightGreen
import com.example.noboros.viewmodel.AddTransactionViewModel

@Composable
fun AddTransactionScreen(
    viewModel: AddTransactionViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showAddAccountDialog by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Text(
            text = "Add Transaction",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = NightGreen,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Transaction Type Selection
        Text(
            text = "Transaction Type",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TransactionType.values().forEach { type ->
                val isSelected = uiState.selectedTransactionType == type
                Button(
                    onClick = { viewModel.updateTransactionType(type) },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (isSelected) NightGreen else Color(0xFF2A2A2A),
                        contentColor = if (isSelected) Color.Black else Color.White
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = type.name,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Amount Input
        OutlinedTextField(
            value = uiState.amount,
            onValueChange = viewModel::updateAmount,
            label = { Text("Amount", color = Color.Gray) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = NightGreen,
                unfocusedBorderColor = Color.Gray,
                textColor = Color.White,
                cursorColor = NightGreen
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Category Selection (for Income/Expense)
        if (uiState.selectedTransactionType != TransactionType.TRANSFER) {
            Text(
                text = "Category",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            var expandedCategory by remember { mutableStateOf(false) }

            Box {
                OutlinedTextField(
                    value = uiState.selectedCategory?.displayName ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Category", color = Color.Gray) },
                    trailingIcon = {
                        IconButton(onClick = { expandedCategory = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray)
                        }
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = NightGreen,
                        unfocusedBorderColor = Color.Gray,
                        textColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                DropdownMenu(
                    expanded = expandedCategory,
                    onDismissRequest = { expandedCategory = false },
                    modifier = Modifier.background(Color(0xFF2A2A2A))
                ) {
                    TransactionCategory.values().forEach { category ->
                        DropdownMenuItem(
                            onClick = {
                                viewModel.updateCategory(category)
                                expandedCategory = false
                            }
                        ) {
                            Text(category.displayName, color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Account Selection
            Text(
                text = "Account",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            var expandedAccount by remember { mutableStateOf(false) }

            Box {
                OutlinedTextField(
                    value = uiState.selectedAccount?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Account", color = Color.Gray) },
                    trailingIcon = {
                        IconButton(onClick = { expandedAccount = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray)
                        }
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = NightGreen,
                        unfocusedBorderColor = Color.Gray,
                        textColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                DropdownMenu(
                    expanded = expandedAccount,
                    onDismissRequest = { expandedAccount = false },
                    modifier = Modifier.background(Color(0xFF2A2A2A))
                ) {
                    uiState.accounts.forEach { account ->
                        DropdownMenuItem(
                            onClick = {
                                viewModel.updateAccount(account)
                                expandedAccount = false
                            }
                        ) {
                            Text(account.name, color = Color.White)
                        }
                    }
                }
            }
        }

        // Transfer Account Selection
        if (uiState.selectedTransactionType == TransactionType.TRANSFER) {
            // Source Account
            Text(
                text = "From Account",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            var expandedSourceAccount by remember { mutableStateOf(false) }

            Box {
                OutlinedTextField(
                    value = uiState.selectedSourceAccount?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Source Account", color = Color.Gray) },
                    trailingIcon = {
                        IconButton(onClick = { expandedSourceAccount = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray)
                        }
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = NightGreen,
                        unfocusedBorderColor = Color.Gray,
                        textColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                DropdownMenu(
                    expanded = expandedSourceAccount,
                    onDismissRequest = { expandedSourceAccount = false },
                    modifier = Modifier.background(Color(0xFF2A2A2A))
                ) {
                    uiState.accounts.forEach { account ->
                        DropdownMenuItem(
                            onClick = {
                                viewModel.updateSourceAccount(account)
                                expandedSourceAccount = false
                            }
                        ) {
                            Text(account.name, color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Destination Account
            Text(
                text = "To Account",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            var expandedDestAccount by remember { mutableStateOf(false) }

            Box {
                OutlinedTextField(
                    value = uiState.selectedDestinationAccount?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Destination Account", color = Color.Gray) },
                    trailingIcon = {
                        IconButton(onClick = { expandedDestAccount = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray)
                        }
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = NightGreen,
                        unfocusedBorderColor = Color.Gray,
                        textColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                DropdownMenu(
                    expanded = expandedDestAccount,
                    onDismissRequest = { expandedDestAccount = false },
                    modifier = Modifier.background(Color(0xFF2A2A2A))
                ) {
                    uiState.accounts.forEach { account ->
                        DropdownMenuItem(
                            onClick = {
                                viewModel.updateDestinationAccount(account)
                                expandedDestAccount = false
                            }
                        ) {
                            Text(account.name, color = Color.White)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Description Input
        OutlinedTextField(
            value = uiState.description,
            onValueChange = viewModel::updateDescription,
            label = { Text("Description (Optional)", color = Color.Gray) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = NightGreen,
                unfocusedBorderColor = Color.Gray,
                textColor = Color.White,
                cursorColor = NightGreen
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        // Add Account Button
        OutlinedButton(
            onClick = { showAddAccountDialog = true },
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = NightGreen
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Add New Account")
        }

        // Submit Button
        Button(
            onClick = {
                if (viewModel.addTransaction()) {
                    showSuccessMessage = true
                }
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = NightGreen,
                contentColor = Color.Black
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Add Transaction",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }

    // Add Account Dialog
    if (showAddAccountDialog) {
        AddAccountDialog(
            onDismiss = { showAddAccountDialog = false },
            onAddAccount = { name, category, balance ->
                viewModel.addAccount(name, category, balance)
                showAddAccountDialog = false
            }
        )
    }

    // Success Message
    if (showSuccessMessage) {
        LaunchedEffect(showSuccessMessage) {
            kotlinx.coroutines.delay(2000)
            showSuccessMessage = false
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            backgroundColor = NightGreen
        ) {
            Text(
                text = "Transaction added successfully!",
                color = Color.Black,
                modifier = Modifier.padding(16.dp),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun AddAccountDialog(
    onDismiss: () -> Unit,
    onAddAccount: (String, AccountCategory, Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(AccountCategory.GENERAL) }
    var initialBalance by remember { mutableStateOf("") }
    var expandedCategory by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Account", color = Color.White) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Account Name", color = Color.Gray) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = NightGreen,
                        unfocusedBorderColor = Color.Gray,
                        textColor = Color.White,
                        cursorColor = NightGreen
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                Box {
                    OutlinedTextField(
                        value = selectedCategory.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category", color = Color.Gray) },
                        trailingIcon = {
                            IconButton(onClick = { expandedCategory = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray)
                            }
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = NightGreen,
                            unfocusedBorderColor = Color.Gray,
                            textColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    DropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = { expandedCategory = false },
                        modifier = Modifier.background(Color(0xFF2A2A2A))
                    ) {
                        AccountCategory.values().forEach { category ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedCategory = category
                                    expandedCategory = false
                                }
                            ) {
                                Text(category.displayName, color = Color.White)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = initialBalance,
                    onValueChange = { initialBalance = it },
                    label = { Text("Initial Balance", color = Color.Gray) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = NightGreen,
                        unfocusedBorderColor = Color.Gray,
                        textColor = Color.White,
                        cursorColor = NightGreen
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val balance = initialBalance.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank()) {
                        onAddAccount(name, selectedCategory, balance)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = NightGreen,
                    contentColor = Color.Black
                )
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)
            ) {
                Text("Cancel")
            }
        },
        backgroundColor = Color(0xFF1A1A1A)
    )
}
