package com.pinu.ai_integration_demo_project.data.tool_executors

import com.google.firebase.ai.type.FunctionDeclaration
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.Tool

object BankingToolDefinitions {

    val getTransactionStatusFunction = FunctionDeclaration(
        name = "getTransactionStatus",
        description = "Get the status and details of a transaction using its transaction ID.",
        parameters = mapOf(
            "transactionId" to Schema.string(description = "The unique transaction ID.")
        )
    )

    val getAccountBalanceFunction = FunctionDeclaration(
        name = "getAccountBalance",
        description = "Get the current balance and details of a bank account using its account ID.",
        parameters = mapOf(
            "accountId" to Schema.string(
                description = "The unique ID of the bank account."
            )
        )
    )
}

object ToolCalls {
    val bankingTool = Tool.functionDeclarations(
        listOf(BankingToolDefinitions.getTransactionStatusFunction,
            BankingToolDefinitions.getAccountBalanceFunction)
    )

}

enum class ToolName(val value: String){
    transactionStatus("getTransactionStatus"), accountBalance("getAccountBalance")
}