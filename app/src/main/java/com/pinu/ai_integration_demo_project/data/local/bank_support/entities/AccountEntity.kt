package com.pinu.ai_integration_demo_project.data.local.bank_support.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "account")
data class AccountEntity(
    @PrimaryKey
    val accountId: String,
    val accountNumber: String,
    val accountType: String,
    val balance: Double,
    val currency: String,
    val status: String
)