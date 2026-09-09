package com.pinu.ai_integration_demo_project.ui.chatlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pinu.ai_integration_demo_project.ui.ChatViewModel
import com.pinu.ai_integration_demo_project.ui.create.CreateAssistantDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    viewModel: ChatViewModel,
    onChatSelected: (String) -> Unit
) {
    val chats by viewModel.chats.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Assistant")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(chats) { chat ->
                ListItem(
                    headlineContent = { Text(chat.name) },
                    supportingContent = { Text(chat.role) },
                    modifier = Modifier.clickable { onChatSelected(chat.id) }
                )
                HorizontalDivider()
            }
        }
    }

    if (showDialog) {
        CreateAssistantDialog(
            onDismiss = { showDialog = false },
            onCreate = { name, role ->
                viewModel.createChat(name, role)
                showDialog = false
            }
        )
    }
}
