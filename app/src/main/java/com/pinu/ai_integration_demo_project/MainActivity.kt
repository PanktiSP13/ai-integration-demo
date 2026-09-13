package com.pinu.ai_integration_demo_project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.pinu.ai_integration_demo_project.data.local.AppDatabase
import com.pinu.ai_integration_demo_project.data.repository.AIRepository
import com.pinu.ai_integration_demo_project.data.repository.bank_support.TransactionRepository
import com.pinu.ai_integration_demo_project.data.repository.chat_support.ChatRepository
import com.pinu.ai_integration_demo_project.data.tool_executors.BankingToolExecutor
import com.pinu.ai_integration_demo_project.ui.ChatViewModel
import com.pinu.ai_integration_demo_project.ui.navigation.ChatNavigation
import com.pinu.ai_integration_demo_project.ui.theme.AI_Integration_Demo_ProjectTheme
import com.pinu.ai_integration_demo_project.ui.utils.BankingMockDataLoader
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val database by lazy { AppDatabase.getDatabase(this) }
    private val repository by lazy { ChatRepository(database.chatDao(), database.messageDao()) }
    private val transactionRepository by lazy { TransactionRepository(database.transactionDao()) }
    private val aiRepository by lazy { AIRepository(repository, BankingToolExecutor(transactionRepository)) }
    private val viewModel by lazy { ChatViewModel(repository, aiRepository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Bank App Support : Load mock data for transactions
        lifecycleScope.launch {
            BankingMockDataLoader(context = this@MainActivity, transactionRepository).load()
        }

        setContent {
            AI_Integration_Demo_ProjectTheme {
                ChatNavigation(viewModel = viewModel)
            }
        }
    }
}
