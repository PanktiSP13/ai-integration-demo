package com.pinu.ai_integration_demo_project.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pinu.ai_integration_demo_project.data.local.dao.ChatDao
import com.pinu.ai_integration_demo_project.data.local.dao.MessageDao
import com.pinu.ai_integration_demo_project.data.local.entities.ChatEntity
import com.pinu.ai_integration_demo_project.data.local.entities.MessageEntity

@Database(entities = [ChatEntity::class, MessageEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "chat_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
