package com.example.noboros.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.noboros.ui.theme.NightGreen
import com.example.noboros.viewmodel.BalancePoint
import com.example.noboros.viewmodel.StatisticsViewModel
import java.text.NumberFormat
import java.util.*

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = viewModel()
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
            text = "Statistics",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = NightGreen,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Current Balance Card
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
                    text = "Current Total Balance",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = formatCurrency(uiState.totalBalance),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (uiState.totalBalance >= 0) NightGreen else Color.Red,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Balance Trend Chart
        Text(
            text = "Balance Trend",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            backgroundColor = Color(0xFF1A1A1A),
            elevation = 4.dp
        ) {
            if (uiState.balanceHistory.isNotEmpty()) {
                BalanceChart(
                    balanceHistory = uiState.balanceHistory,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No transaction data available",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Summary Statistics
        if (uiState.balanceHistory.isNotEmpty()) {
            val minBalance = uiState.balanceHistory.minOfOrNull { it.balance } ?: 0.0
            val maxBalance = uiState.balanceHistory.maxOfOrNull { it.balance } ?: 0.0

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Highest",
                    value = formatCurrency(maxBalance),
                    color = NightGreen,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Lowest",
                    value = formatCurrency(minBalance),
                    color = if (minBalance >= 0) NightGreen else Color.Red,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun BalanceChart(
    balanceHistory: List<BalancePoint>,
    modifier: Modifier = Modifier
) {
    if (balanceHistory.isEmpty()) return

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val padding = 40.dp.toPx()

        val chartWidth = width - padding * 2
        val chartHeight = height - padding * 2

        val minBalance = balanceHistory.minOfOrNull { it.balance } ?: 0.0
        val maxBalance = balanceHistory.maxOfOrNull { it.balance } ?: 0.0
        val balanceRange = maxBalance - minBalance

        if (balanceRange == 0.0) {
            // Draw horizontal line for constant balance
            val y = padding + chartHeight / 2
            drawLine(
                color = NightGreen,
                start = Offset(padding, y),
                end = Offset(width - padding, y),
                strokeWidth = 3.dp.toPx()
            )
            return@Canvas
        }

        // Draw axes
        drawLine(
            color = Color.Gray,
            start = Offset(padding, padding),
            end = Offset(padding, height - padding),
            strokeWidth = 2.dp.toPx()
        )
        drawLine(
            color = Color.Gray,
            start = Offset(padding, height - padding),
            end = Offset(width - padding, height - padding),
            strokeWidth = 2.dp.toPx()
        )

        // Draw zero line if needed
        if (minBalance < 0 && maxBalance > 0) {
            val zeroY = padding + chartHeight * (maxBalance / balanceRange).toFloat()
            drawLine(
                color = Color.Red.copy(alpha = 0.5f),
                start = Offset(padding, zeroY),
                end = Offset(width - padding, zeroY),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Calculate points
        val points = balanceHistory.mapIndexed { index, point ->
            val x = padding + (index.toFloat() / (balanceHistory.size - 1)) * chartWidth
            val y = padding + ((maxBalance - point.balance) / balanceRange).toFloat() * chartHeight
            Offset(x, y)
        }

        // Draw line
        if (points.size > 1) {
            val path = Path()
            path.moveTo(points[0].x, points[0].y)
            for (i in 1 until points.size) {
                path.lineTo(points[i].x, points[i].y)
            }

            drawPath(
                path = path,
                color = NightGreen,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
            )
        }

        // Draw points
        points.forEach { point ->
            drawCircle(
                color = NightGreen,
                radius = 4.dp.toPx(),
                center = point
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        backgroundColor = Color(0xFF2A2A2A),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = color,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return formatter.format(amount)
}