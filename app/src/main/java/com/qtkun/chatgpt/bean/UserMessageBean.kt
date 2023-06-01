package com.qtkun.chatgpt.bean

data class UserMessageBean(
    val id: String = "",
    val content: String,
    val role: String
) {
    fun mapToChatMessage(): ChatMessageBean {
        return ChatMessageBean(id, content, role)
    }
}

object Role {
    const val ASSISTANT = "assistant"
    const val USER = "user"
}