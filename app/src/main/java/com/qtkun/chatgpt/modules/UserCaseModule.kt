package com.qtkun.chatgpt.modules

import com.qtkun.chatgpt.bean.ChatMessageBean
import com.qtkun.chatgpt.bean.Role
import com.qtkun.chatgpt.bean.UserMessageBean
import com.qtkun.chatgpt.domain.GetHistoryMessageUserCase
import com.qtkun.chatgpt.domain.SaveUserMessageUserCase
import com.qtkun.chatgpt.domain.SendMessageAndSaveUserCase
import com.qtkun.chatgpt.net.ApiResult
import com.qtkun.chatgpt.repository.ChatGPTRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.util.UUID

@Module
@InstallIn(ViewModelComponent::class)
object UserCaseModule {
    @Provides
    fun providerGetHistoryMessageUserCase(chatGPTRepository: ChatGPTRepository) = GetHistoryMessageUserCase {
        chatGPTRepository.getMessageFromRoom()
    }

    @Provides
    fun providerSendMessageAndSaveUserCase(chatGPTRepository: ChatGPTRepository) = SendMessageAndSaveUserCase { content ->
        chatGPTRepository.sendMessageToChatGPT(content)
            .map {
                if (it is ApiResult.Success) {
                    it.data?.let { chatBean ->
                        var receiveContent = ""
                        var role = ""
                        if (chatBean.choices.isNotEmpty()) {
                            receiveContent = chatBean.choices.first().message?.content ?: ""
                            role = chatBean.choices.first().message?.role ?: Role.ASSISTANT
                        }
                        val messageBean = ChatMessageBean(chatBean.id, receiveContent, role)
                        chatGPTRepository.insertMessage(messageBean)
                        messageBean
                    }
                } else null
            }
    }

    @Provides
    fun providerSaveUserMessageUserCase(chatGPTRepository: ChatGPTRepository) = SaveUserMessageUserCase { content ->
        flow {
            val message = UserMessageBean(UUID.randomUUID().toString(), content, Role.USER)
            emit(message)
            chatGPTRepository.insertMessage(message.mapToChatMessage())
        }.flowOn(Dispatchers.IO)
    }
}