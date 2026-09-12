package com.pinu.ai_integration_demo_project.data.model.bank_support

data class TransactionAnalysis(
    val category: String,
    val issue: String,
    val priority: String,
    val requiresHumanSupport: Boolean,
    val amount: Int?
)