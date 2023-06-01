package com.qtkun.chatgpt.bean

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "chat_message")
data class ChatMessageBean(
    @PrimaryKey val id: String = "",
    val content: String,
    val role: String
) {
    fun mapToUserMessage(): UserMessageBean {
        return UserMessageBean(id, content, role)
    }
}