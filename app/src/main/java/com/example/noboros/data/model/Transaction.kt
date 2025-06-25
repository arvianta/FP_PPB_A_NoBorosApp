package com.example.noboros.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID

@Parcelize
data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val type: TransactionType,
    val amount: Double,
    val dateTime: Date,  // Using Date instead of LocalDateTime for API 24 compatibility
    val category: TransactionCategory? = null, // For Income/Expense only
    val accountId: String? = null, // For Income/Expense only
    val sourceAccountId: String? = null, // For Transfer only
    val destinationAccountId: String? = null, // For Transfer only
    val description: String = ""
) : Parcelable

enum class TransactionType {
    INCOME, EXPENSE, TRANSFER
}

enum class TransactionCategory(val displayName: String) {
    FOOD_DRINKS("Food & Drinks"),
    SHOPPING("Shopping"),
    HOUSING("Housing"),
    TRANSPORTATION("Transportation"),
    VEHICLE("Vehicle"),
    LIFE_ENTERTAINMENT("Life & Entertainment"),
    FINANCIAL_EXPENSES("Financial expenses"),
    INVESTMENTS("Investments"),
    INCOME("Income"),
    OTHERS("Others")
}
