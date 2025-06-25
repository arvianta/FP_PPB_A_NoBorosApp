package com.example.noboros.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noboros.data.model.Transaction
import com.example.noboros.data.repository.MoneyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Date

class StatisticsViewModel(
    private val repository: MoneyRepository = MoneyRepository.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.accounts,
                repository.transactions
            ) { accounts, transactions ->
                val balanceHistory = calculateBalanceHistory(transactions)
                StatisticsUiState(
                    totalBalance = repository.getTotalBalance(),
                    balanceHistory = balanceHistory
                )
            }.collect {
                _uiState.value = it
            }
        }
    }

    private fun calculateBalanceHistory(transactions: List<Transaction>): List<BalancePoint> {
        if (transactions.isEmpty()) {
            return listOf(BalancePoint(Date(), 0.0))
        }

        val sortedTransactions = transactions.sortedBy { it.dateTime.time }
        val balanceHistory = mutableListOf<BalancePoint>()
        var runningBalance = 0.0

        // Add initial point
        balanceHistory.add(BalancePoint(Date(sortedTransactions.first().dateTime.time - 60000), 0.0))

        sortedTransactions.forEach { transaction ->
            when (transaction.type) {
                com.example.noboros.data.model.TransactionType.INCOME -> {
                    runningBalance += transaction.amount
                }
                com.example.noboros.data.model.TransactionType.EXPENSE -> {
                    runningBalance -= transaction.amount
                }
                com.example.noboros.data.model.TransactionType.TRANSFER -> {
                    // Transfer doesn't change total balance
                }
            }
            balanceHistory.add(BalancePoint(transaction.dateTime, runningBalance))
        }

        return balanceHistory
    }
}

data class StatisticsUiState(
    val totalBalance: Double = 0.0,
    val balanceHistory: List<BalancePoint> = emptyList()
)

data class BalancePoint(
    val dateTime: Date,
    val balance: Double
)