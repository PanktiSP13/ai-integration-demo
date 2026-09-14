package com.pinu.ai_integration_demo_project.data.tool_executors

import android.util.Log
import com.google.firebase.ai.type.FunctionCallPart
import com.google.firebase.ai.type.FunctionResponsePart
import com.pinu.ai_integration_demo_project.data.repository.bank_support.AccountRepository
import com.pinu.ai_integration_demo_project.data.repository.bank_support.TransactionRepository
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class BankingToolExecutor(private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository
) {

    suspend fun execute(functionCall: FunctionCallPart): FunctionResponsePart {

        return when (functionCall.name) {
            ToolName.transactionStatus.value -> getTransactionStatus(functionCall)
            ToolName.accountBalance.value -> getAccountBalance(functionCall)
            else -> unknownFunction(functionCall.name)
        }
    }

    private suspend fun getTransactionStatus(functionCall: FunctionCallPart): FunctionResponsePart {

        val transactionId = functionCall.args["transactionId"]?.toString()?.trim('"')

        if (transactionId.isNullOrEmpty()) {
            return FunctionResponsePart(
                functionCall.name,
                JsonObject(
                    mapOf(
                        "success" to JsonPrimitive(false),
                        "error" to JsonPrimitive("transactionId is missing")
                    )
                )
            )
        }

        val transaction = transactionRepository.getTransactionStatus(transactionId)
        Log.e("AI_FUNCTION", "Transaction = ${transaction.toString()}")


        return if (transaction != null) {
            FunctionResponsePart(
                functionCall.name,
                JsonObject(
                    mapOf(
                        "success" to JsonPrimitive(true),
                        "transactionId" to JsonPrimitive(transaction.transactionId),
                        "accountId" to JsonPrimitive(transaction.accountId),
                        "type" to JsonPrimitive(transaction.type),
                        "amount" to JsonPrimitive(transaction.amount),
                        "status" to JsonPrimitive(transaction.status),
                        "date" to JsonPrimitive(transaction.date),
                        "description" to JsonPrimitive(transaction.description)
                    )
                )
            )
        } else {
            FunctionResponsePart(
                functionCall.name,
                JsonObject(
                    mapOf(
                        "success" to JsonPrimitive(false),
                        "transactionId" to JsonPrimitive(transactionId),
                        "error" to JsonPrimitive("Transaction not found")
                    )
                )
            )
        }
    }

    private suspend fun getAccountBalance(
        functionCall: FunctionCallPart,
    ): FunctionResponsePart {

        val accountId = functionCall.args["accountId"]?.toString()?.trim('"')

        if (accountId.isNullOrEmpty()) {

            return FunctionResponsePart(functionCall.name, JsonObject(
                mapOf(
                        "success" to JsonPrimitive(false),
                        "error" to JsonPrimitive("accountId is missing")
                    )
                )
            )
        }

        val account = accountRepository.getAccountBalance(accountId)

        return if (account != null) {

            FunctionResponsePart(
                functionCall.name,
                JsonObject(
                    mapOf(
                        "success" to JsonPrimitive(true),
                        "accountId" to JsonPrimitive(account.accountId),
                        "accountType" to JsonPrimitive(account.accountType),
                        "balance" to JsonPrimitive(account.balance),
                        "currency" to JsonPrimitive(account.currency),
                    )
                )
            )

        } else {

            FunctionResponsePart(
                functionCall.name, JsonObject(mapOf(
                        "success" to JsonPrimitive(false),
                        "accountId" to JsonPrimitive(accountId),
                        "error" to JsonPrimitive("Account not found")
                    )
                )
            )
        }
    }

    private fun unknownFunction(
        functionName: String,
    ): FunctionResponsePart {

        return FunctionResponsePart(
            functionName,
            JsonObject(
                mapOf(
                    "success" to JsonPrimitive(false),
                    "error" to JsonPrimitive("Unknown function: $functionName")
                )
            )
        )
    }
}