package com.qtkun.chatgpt.service

import com.qtkun.chatgpt.bean.ChatBean
import com.qtkun.chatgpt.net.ApiResult
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatGPTService {
    @POST("v1/chat/completions")
    suspend fun sendMessageToChatGPT(@Body body: RequestBody): ApiResult<ChatBean>
}