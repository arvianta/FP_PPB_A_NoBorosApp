package com.example.noboros.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Account(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: AccountCategory,
    val initialBalance: Double,
    val currentBalance: Double = initialBalance
) : Parcelable

enum class AccountCategory(val displayName: String) {
    GENERAL("General"),
    CASH("Cash"),
    SAVINGS("Savings"),
    INVESTMENT("Investment"),
    MORTGAGE("Mortgage"),
    BONUSES("Bonuses")
}