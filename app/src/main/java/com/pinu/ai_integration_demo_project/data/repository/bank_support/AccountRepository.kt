package com.pinu.ai_integration_demo_project.data.repository.bank_support

import com.pinu.ai_integration_demo_project.data.local.bank_support.dao.AccountDao
import com.pinu.ai_integration_demo_project.data.local.bank_support.entities.AccountEntity
import com.pinu.ai_integration_demo_project.data.local.bank_support.entities.TransactionEntity

class AccountRepository(private val accountDao: AccountDao) {

    suspend fun getAccountBalance(accountId: String): AccountEntity? {
        return accountDao.getAccount(accountId)
    }

    suspend fun insertAccounts(accounts: List<AccountEntity>) {
        accountDao.insertAccounts(accounts)
    }

    suspend fun getAccountCount(): Int {
        return accountDao.getAccountCount()
    }

    suspend fun getMockAccounts(accounts: List<AccountEntity>)  {
        if (getAccountCount() > 0) {
            return
        }

        accountDao.insertAccounts(accounts)
    }
}