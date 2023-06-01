package com.qtkun.chatgpt.base

import androidx.lifecycle.ViewModel
import com.qtkun.chatgpt.GPTApplication
import com.qtkun.chatgpt.ext.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

open class BaseViewModel: ViewModel(){
    private val mutableLoading = MutableStateFlow(false)
    val loading: StateFlow<Boolean>
        get() = mutableLoading

    protected fun<T> Flow<T>.baseLoading(): Flow<T> {
        return flowOn(Dispatchers.IO).onStart {
            mutableLoading.value = true
        }.catch {
            GPTApplication.INSTANCE.toast(it.message ?: "数据异常")
            mutableLoading.value = false
            it.printStackTrace()
        }.onCompletion {
            mutableLoading.value = false
        }
    }

    protected fun<T> Flow<T>.base(): Flow<T> {
        return flowOn(Dispatchers.IO).catch {
            GPTApplication.INSTANCE.toast(it.message ?: "数据异常")
            it.printStackTrace()
        }
    }
}
