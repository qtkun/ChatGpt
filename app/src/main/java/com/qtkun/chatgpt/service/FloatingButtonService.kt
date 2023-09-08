package com.qtkun.chatgpt.service

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.ColorDrawable
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.core.content.getSystemService
import com.qtkun.chatgpt.bean.Role
import com.qtkun.chatgpt.bean.UserMessageBean
import com.qtkun.chatgpt.databinding.FloatingButtonLayoutBinding
import com.qtkun.chatgpt.databinding.PopLayoutBinding
import com.qtkun.chatgpt.ext.dp
import com.qtkun.chatgpt.ext.singleClick
import com.qtkun.chatgpt.net.ApiResult
import com.qtkun.chatgpt.repository.ChatGPTRepository
import com.qtkun.chatgpt.utils.GlobalDialogBean
import com.qtkun.chatgpt.utils.GlobalDialogManager
import com.qtkun.chatgpt.utils.GlobalDialogType
import com.qtkun.chatgpt.widget.showTop
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FloatingButtonService: Service() {
    @Inject
    lateinit var chatGPTRepository: ChatGPTRepository

    private var windowManager: WindowManager? = null
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FloatingButtonLayoutBinding.inflate(LayoutInflater.from(this))
    }
    private lateinit var popupWindow: PopupWindow
    private val popBinding: PopLayoutBinding by lazy(LazyThreadSafetyMode.NONE) {
        PopLayoutBinding.inflate(LayoutInflater.from(this))
    }

    private val scope = MainScope()

    override fun onCreate() {
        super.onCreate()

        windowManager = getSystemService()
        popupWindow = PopupWindow(this).apply {
            this.contentView = popBinding.root
            width = ViewGroup.LayoutParams.WRAP_CONTENT
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            isOutsideTouchable = true
            isFocusable = true
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        popBinding.q1.singleClick {
            question(popBinding.q1.text.toString())
            popupWindow.dismiss()
        }
        popBinding.q2.singleClick {
            question(popBinding.q2.text.toString())
            popupWindow.dismiss()
        }
        popBinding.q3.singleClick {
            question(popBinding.q3.text.toString())
            popupWindow.dismiss()
        }
        val params = WindowManager.LayoutParams(
            100.dp,
            100.dp,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.END or Gravity.BOTTOM
        }
        binding.lottie.playAnimation()
        binding.root.singleClick {
            popBinding.tc.showTop(popupWindow, binding.root)
        }
        windowManager?.addView(binding.root, params)
    }

    private fun question(text: String) {
        scope.launch {
            chatGPTRepository.sendMessageToChatGPT(
                listOf(
                    UserMessageBean(content = text, role = Role.USER)
                )
            ).filter {
                it is ApiResult.Success
            }.collectLatest {
                (it as ApiResult.Success).data?.let { chatBean ->
                    var receiveContent = ""
                    if (chatBean.choices.isNotEmpty()) {
                        receiveContent = chatBean.choices.first().message?.content.orEmpty()
                    }
                    GlobalDialogManager.instance.sendDialogRequest(
                        GlobalDialogBean(
                            GlobalDialogType.WARNING,
                            "",
                            receiveContent,
                            null,
                            false
                        )
                    )
                }

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.lottie.playAnimation()
        windowManager?.removeView(binding.root)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}