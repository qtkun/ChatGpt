package com.qtkun.chatgpt.viewmodel

import androidx.lifecycle.viewModelScope
import com.qtkun.chatgpt.base.BaseViewModel
import com.qtkun.chatgpt.bean.ChatMessageBean
import com.qtkun.chatgpt.bean.Role
import com.qtkun.chatgpt.bean.StreamChatBean
import com.qtkun.chatgpt.domain.GetHistoryMessageUserCase
import com.qtkun.chatgpt.domain.SaveUserMessageUserCase
import com.qtkun.chatgpt.domain.SendMessageAndSaveUserCase
import com.qtkun.chatgpt.ext.fromJson
import com.qtkun.chatgpt.repository.ChatGPTRepository
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

@HiltViewModel
class ChatGPTViewModel @Inject constructor(
    private val chatGPTRepository: ChatGPTRepository,
    private val getHistoryMessageUserCase: GetHistoryMessageUserCase,
    private val sendMessageAndSaveUserCase: SendMessageAndSaveUserCase,
    private val saveUserMessageUserCase: SaveUserMessageUserCase
) : BaseViewModel() {
    @Inject
    lateinit var moshi: Moshi
    val message = MutableStateFlow<Any?>(null)
    val streamMessage = MutableStateFlow<ChatMessageBean?>(null)

    val historyMessage = MutableStateFlow<List<Any>>(emptyList())

    fun sendMessageToChatGPT(messageList: List<Any>) = viewModelScope.launch {
        sendMessageAndSaveUserCase(messageList)
            .baseLoading()
            .collect {
                message.value = it
            }
    }

    fun sendMessageToChatGPTStream(messageList: List<Any>) = viewModelScope.launch {

        chatGPTRepository.sendMessageToChatGPTStream(messageList).let { response ->
            response.byteStream().use { ips ->
                val bufferedReader = BufferedReader(InputStreamReader(ips))
                var line: String? = null
                while (bufferedReader.readLine()?.also { line = it } != null) {
                    line?.let { dataStr ->
                        if (dataStr.isNotEmpty() && !dataStr.contains("done", true)) {
                            moshi.fromJson<StreamChatBean>(dataStr.removePrefix("data: "))?.let { data ->
                                data.choices?.firstOrNull()?.let { choice ->
                                    if (choice.finish_reason == null) {
                                        if (choice.delta?.role?.isNotEmpty() == true) {
                                            streamMessage.value = ChatMessageBean(
                                                data.id,
                                                "",
                                                choice.delta.role
                                            )
                                        } else {
                                            streamMessage.value = ChatMessageBean(
                                                data.id,
                                                "${streamMessage.value?.content.orEmpty()}${choice.delta?.content.orEmpty()}",
                                                streamMessage.value?.role ?: Role.ASSISTANT
                                            )
                                        }
                                    } else {
                                        chatGPTRepository.insertMessage(streamMessage.value!!)
                                    }
                                }
                            }
                        }
                    }
                }
            } }
    }

    fun getMessageFromRoom() = viewModelScope.launch {
        getHistoryMessageUserCase()
            .baseLoading()
            .collect {
                historyMessage.value = it
            }
    }

    fun saveUserMessage(content: String) = viewModelScope.launch {
        message.value = saveUserMessageUserCase(content).first()
    }

    fun deleteAllMessages() = viewModelScope.launch {
        chatGPTRepository.deleteAllMessages()
    }

    fun deleteMessage(vararg messages: ChatMessageBean) = viewModelScope.launch {
        chatGPTRepository.deleteMessages(*messages)
    }
}