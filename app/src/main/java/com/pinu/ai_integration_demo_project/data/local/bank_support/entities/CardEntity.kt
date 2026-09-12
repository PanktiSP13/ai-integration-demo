package com.pinu.ai_integration_demo_project.data.local.bank_support.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "card")
data class CardEntity(
    @PrimaryKey
    val cardId: String,
    val accountId: String,
    val cardType: String,
    val lastFourDigits: String,
    val status: String
)