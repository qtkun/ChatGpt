package com.qtkun.chatgpt.domain

import com.qtkun.chatgpt.bean.ChatMessageBean
import com.qtkun.chatgpt.bean.UserMessageBean
import kotlinx.coroutines.flow.Flow

fun interface GetHistoryMessageUserCase : () -> Flow<List<Any>>

fun interface SendMessageAndSaveUserCase: (List<Any>) -> Flow<ChatMessageBean?>

fun interface SaveUserMessageUserCase: (String) -> Flow<UserMessageBean>