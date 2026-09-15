package com.pinu.ai_integration_demo_project.ui.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pinu.ai_integration_demo_project.ui.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: ChatViewModel,
    onNavigateToChats: () -> Unit,
    onNavigateToBankingSupportChat: () -> Unit,
    onNavigateToBankingAppSupportChat: () -> Unit,
    onNavigateToBankingSupportAIAgent: () -> Unit,
) {
    val chats by viewModel.chats.collectAsStateWithLifecycle()
    val chatCount = chats.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Personal Assistants") },
                actions = {
                    BadgedBox(
                        badge = {
                            if (chatCount > 0) {
                                Badge { Text(chatCount.toString()) }
                            }
                        },
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        IconButton(onClick = onNavigateToChats) {
                            Icon(Icons.Default.Chat, contentDescription = "Chats")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth()
        ) {
            Button(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                onClick = {
                    onNavigateToBankingSupportChat()
                }) {
                Text("Banking Support Chat \n (Structured Output)")
            }
            Button(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                onClick = {
                    onNavigateToBankingAppSupportChat()
                }) {
                Text("Banking Support Chat \n (Function Calling)")
            }
            Button(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                onClick = {
                    onNavigateToBankingSupportAIAgent()
                }) {
                Text("Banking Support AI Agent \n (Agent)")
            }
        }

    }
}
