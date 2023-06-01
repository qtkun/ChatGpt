package com.qtkun.chatgpt.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.qtkun.chatgpt.bean.ChatMessageBean

@Database(version = 1, entities = [ChatMessageBean::class])
@TypeConverters(Converter:: class)
abstract class AppDataBase: RoomDatabase() {
    abstract fun getChatGPTDao(): ChatGPTDao
}