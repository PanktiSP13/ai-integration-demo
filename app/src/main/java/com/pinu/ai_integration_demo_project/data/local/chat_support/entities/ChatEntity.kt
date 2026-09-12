package com.pinu.ai_integration_demo_project.data.local.chat_support.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val role: String
)
