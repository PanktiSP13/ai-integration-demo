package com.pinu.ai_integration_demo_project.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.pinu.ai_integration_demo_project.data.local.bank_support.dao.TransactionDao
import com.pinu.ai_integration_demo_project.data.local.bank_support.entities.TransactionEntity
import com.pinu.ai_integration_demo_project.data.local.chat_support.Converters
import com.pinu.ai_integration_demo_project.data.local.chat_support.dao.ChatDao
import com.pinu.ai_integration_demo_project.data.local.chat_support.dao.MessageDao
import com.pinu.ai_integration_demo_project.data.local.chat_support.entities.ChatEntity
import com.pinu.ai_integration_demo_project.data.local.chat_support.entities.MessageEntity

@Database(entities = [ChatEntity::class, MessageEntity::class, TransactionEntity::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ai_demo_database").addCallback(object : Callback() {
                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            db.execSQL("PRAGMA foreign_keys = ON;")
                        }
                    }).fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
