package com.qtkun.chatgpt.utils

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.annotation.DrawableRes
import androidx.lifecycle.Lifecycle
import com.blankj.utilcode.util.ActivityUtils
import com.qtkun.chatgpt.dialog.NoticeDialog
import com.qtkun.chatgpt.dialog.WarningDialog
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class GlobalDialogManager {
    companion object {
        val instance by lazy {
            GlobalDialogManager()
        }
    }
    private val dialogQueue = Channel<GlobalDialogBean>(UNLIMITED)
    private var isShowing: Boolean = false
    private val mutex = Mutex()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default + CoroutineName("GlobalDialogManager"))

    init {
        loop()
    }

    fun sendDialogRequest(dialogBean: GlobalDialogBean) {
        scope.launch {
            dialogQueue.send(dialogBean)
        }
    }

    private fun loop() {
        scope.launch {
            while (true) {
                val dialogBean = dialogQueue.receive()
                (ActivityUtils.getTopActivity() as? ComponentActivity)?.let { activity ->
                    mutex.withLock {
                        if (!isShowing && activity.lifecycle.currentState > Lifecycle.State.STARTED) {
                            scope.launch(Dispatchers.Main) {
                                val dialog = getDialog(activity, dialogBean)
                                if (dialog.handleDialog(dialogBean)) {
                                    isShowing = true
                                    dialog.showDialog(dialogBean)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getDialog(context: Context, dialogBean: GlobalDialogBean): IGlobalDialog {
        return when(dialogBean.type) {
            GlobalDialogType.WARNING -> WarningDialog(context)
            GlobalDialogType.NOTICE -> NoticeDialog(context)
        }
    }

    fun dismissDialog() {
        scope.launch {
            mutex.withLock {
                isShowing = false
            }
        }
    }
}

data class GlobalDialogBean(
    val type: GlobalDialogType,
    val title: String?,
    val content: String?,
    @DrawableRes val icon: Int?,
    val isAutoDismiss: Boolean = true
)

interface IGlobalDialog {
    fun showDialog(globalDialogBean: GlobalDialogBean)
    fun handleDialog(globalDialogBean: GlobalDialogBean): Boolean
}

enum class GlobalDialogType{
    WARNING,
    NOTICE
}