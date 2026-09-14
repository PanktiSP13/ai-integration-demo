package com.pinu.ai_integration_demo_project.data.local.bank_support.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pinu.ai_integration_demo_project.data.local.bank_support.entities.AccountEntity

@Dao
interface AccountDao {

    @Query("""SELECT * FROM account WHERE accountId = :accountId""")
    suspend fun getAccount(accountId: String): AccountEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAccounts(accounts: List<AccountEntity> )

    @Query("SELECT COUNT(*) FROM account")
    suspend fun getAccountCount(): Int
}