package com.pinu.ai_integration_demo_project.ui.dashboard

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pinu.ai_integration_demo_project.ui.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: ChatViewModel,
    onNavigateToChats: () -> Unit,
    content: @Composable (Modifier) -> Unit
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
        content(Modifier.padding(padding))
    }
}
