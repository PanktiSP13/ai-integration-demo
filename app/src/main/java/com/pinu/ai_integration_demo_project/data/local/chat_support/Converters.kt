package com.pinu.ai_integration_demo_project.data.local.chat_support

import androidx.room.TypeConverter
import com.pinu.ai_integration_demo_project.data.model.SenderType

class Converters {
    @TypeConverter
    fun fromSenderType(value: SenderType): String {
        return value.name
    }

    @TypeConverter
    fun toSenderType(value: String): SenderType {
        return SenderType.valueOf(value)
    }
}