package com.pinu.ai_integration_demo_project.data.local.bank_support.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "beneficiary")
data class BeneficiaryEntity(
    @PrimaryKey
    val beneficiaryId: String,
    val customerId: String,
    val name: String,
    val bankName: String,
    val accountLastFour: String
)