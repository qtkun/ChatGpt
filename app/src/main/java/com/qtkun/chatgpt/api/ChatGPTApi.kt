package com.qtkun.chatgpt.api

import com.qtkun.chatgpt.bean.ChatBean
import com.qtkun.chatgpt.bean.ChatMessageBean
import com.qtkun.chatgpt.bean.UserMessageBean
import com.qtkun.chatgpt.ext.createBody
import com.qtkun.chatgpt.net.ApiResult
import com.qtkun.chatgpt.service.ChatGPTService
import com.squareup.moshi.Moshi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatGPTApi @Inject constructor(private val service: ChatGPTService){
    @Inject
    lateinit var moshi: Moshi

    suspend fun sendMessageToChatGPT(messageList: List<Any>): ApiResult<ChatBean> {
        val messages = mutableListOf<Map<String, Any>>()
        for (data in messageList) {
            when(data) {
                is ChatMessageBean -> {
                    messages.add(mapOf(
                        "role" to data.role,
                        "content" to data.content
                    ))
                }
                is UserMessageBean -> {
                    messages.add(mapOf(
                        "role" to data.role,
                        "content" to data.content
                    ))
                }
            }
        }
        val params = mapOf(
            "model" to "gpt-3.5-turbo",
            "messages" to messages
        )
        return service.sendMessageToChatGPT(moshi.createBody(params))
    }
}