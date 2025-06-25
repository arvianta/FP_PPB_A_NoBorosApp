package com.example.noboros.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noboros.data.model.Account
import com.example.noboros.data.model.AccountCategory
import com.example.noboros.data.model.Transaction
import com.example.noboros.data.model.TransactionCategory
import com.example.noboros.data.model.TransactionType
import com.example.noboros.data.repository.MoneyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class AddTransactionViewModel(
    private val repository: MoneyRepository = MoneyRepository.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.accounts.collect { accounts ->
                _uiState.value = _uiState.value.copy(accounts = accounts)
            }
        }
    }

    fun updateTransactionType(type: TransactionType) {
        _uiState.value = _uiState.value.copy(
            selectedTransactionType = type,
            selectedCategory = null,
            selectedAccount = null,
            selectedSourceAccount = null,
            selectedDestinationAccount = null
        )
    }

    fun updateAmount(amount: String) {
        _uiState.value = _uiState.value.copy(amount = amount)
    }

    fun updateDescription(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun updateCategory(category: TransactionCategory) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun updateAccount(account: Account) {
        _uiState.value = _uiState.value.copy(selectedAccount = account)
    }

    fun updateSourceAccount(account: Account) {
        _uiState.value = _uiState.value.copy(selectedSourceAccount = account)
    }

    fun updateDestinationAccount(account: Account) {
        _uiState.value = _uiState.value.copy(selectedDestinationAccount = account)
    }

    fun updateDateTime(dateTime: Date) {
        _uiState.value = _uiState.value.copy(dateTime = dateTime)
    }

    fun addAccount(name: String, category: AccountCategory, initialBalance: Double) {
        val account = Account(
            name = name,
            category = category,
            initialBalance = initialBalance,
            currentBalance = initialBalance
        )
        repository.addAccount(account)
    }

    fun addTransaction(): Boolean {
        val state = _uiState.value
        val amount = state.amount.toDoubleOrNull() ?: return false

        val transaction = when (state.selectedTransactionType) {
            TransactionType.INCOME, TransactionType.EXPENSE -> {
                if (state.selectedAccount == null || state.selectedCategory == null) return false
                Transaction(
                    type = state.selectedTransactionType,
                    amount = amount,
                    dateTime = state.dateTime,
                    category = state.selectedCategory,
                    accountId = state.selectedAccount.id,
                    description = state.description
                )
            }
            TransactionType.TRANSFER -> {
                if (state.selectedSourceAccount == null || state.selectedDestinationAccount == null) return false
                if (state.selectedSourceAccount.id == state.selectedDestinationAccount.id) return false
                Transaction(
                    type = state.selectedTransactionType,
                    amount = amount,
                    dateTime = state.dateTime,
                    sourceAccountId = state.selectedSourceAccount.id,
                    destinationAccountId = state.selectedDestinationAccount.id,
                    description = state.description
                )
            }
        }

        repository.addTransaction(transaction)
        resetForm()
        return true
    }

    private fun resetForm() {
        _uiState.value = AddTransactionUiState(
            accounts = _uiState.value.accounts,
            dateTime = Date()
        )
    }
}

data class AddTransactionUiState(
    val accounts: List<Account> = emptyList(),
    val selectedTransactionType: TransactionType = TransactionType.EXPENSE,
    val amount: String = "",
    val dateTime: Date = Date(),
    val selectedCategory: TransactionCategory? = null,
    val selectedAccount: Account? = null,
    val selectedSourceAccount: Account? = null,
    val selectedDestinationAccount: Account? = null,
    val description: String = ""
)