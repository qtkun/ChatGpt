package com.qtkun.chatgpt.service

import com.qtkun.chatgpt.bean.ChatBean
import com.qtkun.chatgpt.net.ApiResult
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Streaming

interface ChatGPTService {
    @POST("v1/chat/completions")
    suspend fun sendMessageToChatGPT(@Body body: RequestBody): ApiResult<ChatBean>

    @Streaming
    @POST("v1/chat/completions")
    suspend fun sendMessageToChatGPTStream(@Body body: RequestBody): ResponseBody
}