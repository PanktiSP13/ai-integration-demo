package com.pinu.ai_integration_demo_project.data.local.bank_support.dao

import androidx.room.Dao
import androidx.room.Query
import com.pinu.ai_integration_demo_project.data.local.bank_support.entities.TransactionEntity

@Dao
interface TransactionDao {

    @Query("SELECT * FROM `transaction` WHERE transactionId = :transactionId")
    suspend fun getTransaction(transactionId: String): TransactionEntity?
}