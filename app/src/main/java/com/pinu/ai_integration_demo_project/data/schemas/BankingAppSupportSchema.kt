package com.pinu.ai_integration_demo_project.data.schemas

import com.google.firebase.ai.type.Schema


val transactionSchema = Schema.obj(
    mapOf(
        "category" to Schema.enumeration(
            listOf(
                "UPI_PAYMENT",
                "CARD_PAYMENT",
                "BANK_TRANSFER",
                "OTHER"
            )
        ),

        "issue" to Schema.enumeration(
            listOf(
                "FAILED",
                "DUPLICATE",
                "REFUND_PENDING",
                "UNKNOWN"
            )
        ),

        "priority" to Schema.enumeration(
            listOf(
                "LOW",
                "MEDIUM",
                "HIGH"
            )
        ),

        "requires_human_support" to Schema.boolean(),

        "amount" to Schema.integer(nullable = true)
    )
)