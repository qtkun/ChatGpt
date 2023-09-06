package com.qtkun.chatgpt.repository

import com.qtkun.chatgpt.room.ChatGPTDao
import com.qtkun.chatgpt.api.ChatGPTApi
import com.qtkun.chatgpt.bean.ChatMessageBean
import com.qtkun.chatgpt.bean.Role
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatGPTRepository@Inject constructor(
    private val gptApi: ChatGPTApi,
    private val chatGPTDao: ChatGPTDao
) {
    fun sendMessageToChatGPT(messageList: List<Any>) = flow {
        emit(gptApi.sendMessageToChatGPT(messageList))
    }.flowOn(Dispatchers.IO)

    suspend fun sendMessageToChatGPTStream(messageList: List<Any>) = gptApi.sendMessageToChatGPTStream(messageList)

    fun getMessageFromRoom() = flow {
        emit(chatGPTDao.getChatMessageList())
    }.flowOn(Dispatchers.IO)
        .map {
            it?.map { message ->
                if (message.role == Role.USER) message.mapToUserMessage() else message
            } ?: emptyList()
        }

    suspend fun insertMessage(message: ChatMessageBean) {
        chatGPTDao.insertChatMessage(message)
    }

    suspend fun deleteAllMessages() {
        chatGPTDao.deleteAllMessages()
    }

    suspend fun deleteMessages(vararg messages: ChatMessageBean) {
        chatGPTDao.deleteMessages(*messages)
    }
}