package com.pinu.ai_integration_demo_project.data.repository.bank_support

import com.pinu.ai_integration_demo_project.data.local.bank_support.dao.TransactionDao
import com.pinu.ai_integration_demo_project.data.local.bank_support.entities.TransactionEntity

class TransactionRepository(private val transactionDao: TransactionDao) {

    suspend fun getTransactionStatus(transactionId: String): TransactionEntity? {
        return transactionDao.getTransaction(transactionId)
    }

    suspend fun getMockTransactions(transactions: List<TransactionEntity>) {
        if (transactionDao.getTransactionCount() > 0) {
            return
        }

        transactionDao.insertTransactions(transactions)
    }


    suspend fun getRecentTransactions(accountId: String, limit: Int): List<TransactionEntity> {
        return transactionDao.getRecentTransactions(accountId = accountId, limit = limit)
    }
}
