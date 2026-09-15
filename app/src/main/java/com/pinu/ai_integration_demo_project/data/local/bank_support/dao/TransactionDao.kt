package com.pinu.ai_integration_demo_project.data.local.bank_support.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pinu.ai_integration_demo_project.data.local.bank_support.entities.TransactionEntity

@Dao
interface TransactionDao {

    @Query("SELECT * FROM `transaction` WHERE transactionId = :transactionId")
    suspend fun getTransaction(transactionId: String): TransactionEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>)

    @Query("SELECT COUNT(*) FROM `transaction`")
    suspend fun getTransactionCount(): Int

    @Query("""SELECT * FROM `transaction` WHERE accountId = :accountId ORDER BY date DESC LIMIT :limit""")
    suspend fun getRecentTransactions(accountId: String, limit: Int): List<TransactionEntity>
}