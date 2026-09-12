package com.pinu.ai_integration_demo_project.data.local.bank_support.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transaction")
data class TransactionEntity(
    @PrimaryKey
    val transactionId: String,
    val accountId: String,
    val type: String,
    val amount: Double,
    val status: String,
    val date: String,
    val description: String
)