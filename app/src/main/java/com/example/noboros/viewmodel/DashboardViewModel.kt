package com.example.noboros.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noboros.data.model.Account
import com.example.noboros.data.repository.MoneyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: MoneyRepository = MoneyRepository.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.accounts,
                repository.transactions
            ) { accounts, _ ->
                DashboardUiState(
                    accounts = accounts,
                    totalBalance = repository.getTotalBalance()
                )
            }.collect {
                _uiState.value = it
            }
        }
    }
}

data class DashboardUiState(
    val accounts: List<Account> = emptyList(),
    val totalBalance: Double = 0.0
)