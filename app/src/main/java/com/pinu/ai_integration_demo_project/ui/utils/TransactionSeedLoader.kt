package com.pinu.ai_integration_demo_project.ui.utils

import android.content.Context
import com.google.gson.Gson
import com.pinu.ai_integration_demo_project.data.local.bank_support.entities.TransactionEntity
import com.pinu.ai_integration_demo_project.data.repository.bank_support.TransactionRepository


class BankingMockDataLoader(
    private val context: Context,
    private val transactionRepository: TransactionRepository,
) {

    suspend fun load() {
        val json = context.assets.open("transactions.json").bufferedReader().use { it.readText() }
        val transactions =  Gson().fromJson(json, Array<TransactionEntity>::class.java).toList()
        transactionRepository.getMockTransactions(transactions)
    }
}