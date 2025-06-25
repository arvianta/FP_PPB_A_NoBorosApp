package com.example.noboros.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.noboros.data.model.Account
import com.example.noboros.ui.theme.NightGreen
import com.example.noboros.viewmodel.DashboardViewModel
import java.text.NumberFormat
import java.util.*

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "NoBoros",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = NightGreen,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Total Balance Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            backgroundColor = Color(0xFF1A1A1A),
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Total Balance",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = formatCurrency(uiState.totalBalance),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (uiState.totalBalance >= 0) NightGreen else Color.Red,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Accounts Section
        Text(
            text = "Accounts",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.accounts) { account ->
                AccountCard(account = account)
            }
        }
    }
}

@Composable
fun AccountCard(account: Account) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color(0xFF2A2A2A),
        elevation = 4.dp
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
                    text = account.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Text(
                    text = account.category.displayName,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            Text(
                text = formatCurrency(account.currentBalance),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (account.currentBalance >= 0) NightGreen else Color.Red
            )
        }
    }
}

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return formatter.format(amount)
}