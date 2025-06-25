package com.example.noboros.data.repository

import com.example.noboros.data.model.Account
import com.example.noboros.data.model.AccountCategory
import com.example.noboros.data.model.Transaction
import com.example.noboros.data.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MoneyRepository {

    private val _accounts = MutableStateFlow(
        listOf(
            Account(
                name = "Cash",
                category = AccountCategory.CASH,
                initialBalance = 0.0,
                currentBalance = 0.0
            )
        )
    )
    val accounts: StateFlow<List<Account>> = _accounts.asStateFlow()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    fun addAccount(account: Account) {
        _accounts.value = _accounts.value + account
    }

    fun addTransaction(transaction: Transaction) {
        _transactions.value = _transactions.value + transaction
        updateAccountBalances(transaction)
    }

    private fun updateAccountBalances(transaction: Transaction) {
        val currentAccounts = _accounts.value.toMutableList()

        when (transaction.type) {
            TransactionType.INCOME -> {
                transaction.accountId?.let { accountId ->
                    val accountIndex = currentAccounts.indexOfFirst { it.id == accountId }
                    if (accountIndex != -1) {
                        val account = currentAccounts[accountIndex]
                        currentAccounts[accountIndex] = account.copy(
                            currentBalance = account.currentBalance + transaction.amount
                        )
                    }
                }
            }
            TransactionType.EXPENSE -> {
                transaction.accountId?.let { accountId ->
                    val accountIndex = currentAccounts.indexOfFirst { it.id == accountId }
                    if (accountIndex != -1) {
                        val account = currentAccounts[accountIndex]
                        currentAccounts[accountIndex] = account.copy(
                            currentBalance = account.currentBalance - transaction.amount
                        )
                    }
                }
            }
            TransactionType.TRANSFER -> {
                // Update source account
                transaction.sourceAccountId?.let { sourceId ->
                    val sourceIndex = currentAccounts.indexOfFirst { it.id == sourceId }
                    if (sourceIndex != -1) {
                        val sourceAccount = currentAccounts[sourceIndex]
                        currentAccounts[sourceIndex] = sourceAccount.copy(
                            currentBalance = sourceAccount.currentBalance - transaction.amount
                        )
                    }
                }

                // Update destination account
                transaction.destinationAccountId?.let { destId ->
                    val destIndex = currentAccounts.indexOfFirst { it.id == destId }
                    if (destIndex != -1) {
                        val destAccount = currentAccounts[destIndex]
                        currentAccounts[destIndex] = destAccount.copy(
                            currentBalance = destAccount.currentBalance + transaction.amount
                        )
                    }
                }
            }
        }

        _accounts.value = currentAccounts
    }

    fun getTotalBalance(): Double {
        return _accounts.value.sumOf { it.currentBalance }
    }

    companion object {
        @Volatile
        private var INSTANCE: MoneyRepository? = null

        fun getInstance(): MoneyRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MoneyRepository().also { INSTANCE = it }
            }
        }
    }
}