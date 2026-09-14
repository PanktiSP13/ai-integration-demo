package com.pinu.ai_integration_demo_project.ui.utils

import android.content.Context
import com.google.gson.Gson
import com.pinu.ai_integration_demo_project.data.local.bank_support.entities.AccountEntity
import com.pinu.ai_integration_demo_project.data.local.bank_support.entities.TransactionEntity
import com.pinu.ai_integration_demo_project.data.repository.bank_support.AccountRepository
import com.pinu.ai_integration_demo_project.data.repository.bank_support.TransactionRepository


class BankingMockDataLoader(
    private val context: Context,
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository
) {

    suspend fun load() {
        val transactionResponse = context.assets.open("transactions.json").bufferedReader().use { it.readText() }
        val transactions =  Gson().fromJson(transactionResponse, Array<TransactionEntity>::class.java).toList()
        transactionRepository.getMockTransactions(transactions)


        val accountResponse = context.assets.open("accounts.json").bufferedReader().use { it.readText() }
        val accounts =  Gson().fromJson(accountResponse, Array<AccountEntity>::class.java).toList()
        accountRepository.getMockAccounts(accounts)
    }
}