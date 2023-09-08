package com.qtkun.chatgpt.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatDialog
import com.qtkun.chatgpt.R
import com.qtkun.chatgpt.databinding.DialogNoticeBinding
import com.qtkun.chatgpt.ext.viewBinding
import com.qtkun.chatgpt.utils.GlobalDialogBean
import com.qtkun.chatgpt.utils.GlobalDialogManager
import com.qtkun.chatgpt.utils.IGlobalDialog
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@SuppressLint("ClickableViewAccessibility")
class NoticeDialog @JvmOverloads constructor(
    context: Context,
    @StyleRes themeResId: Int = R.style.CustomDialog
): AppCompatDialog(context, themeResId), IGlobalDialog {
    private val binding by viewBinding<DialogNoticeBinding>()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate + CoroutineName("NoticeDialog"))
    private var dismissJob: Job? = null
    private var downY: Float = 0f
    private var moveY: Float = 0f
    private var isSwipeToDismiss = false

    init {
        binding.root.setOnTouchListener { _, event ->
            handleTouchEvent(event)
            true
        }
        binding.root.setOnClickListener {
            dismiss()
        }
        window?.run {
            setDimAmount(0f)
            setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
            /*setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH)*/
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            attributes.windowAnimations = R.style.NoticeDialogEnter
            setGravity(Gravity.TOP)
        }
        setOnDismissListener {
            if (dismissJob?.isActive == true) {
                dismissJob?.cancel()
                dismissJob = null
            }
            GlobalDialogManager.instance.dismissDialog()
        }
    }

    private fun handleTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downY = event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                moveY = event.rawY
                isSwipeToDismiss = (moveY - downY) < 0f
            }
            MotionEvent.ACTION_UP -> {
                if (isSwipeToDismiss && abs(moveY - downY) > 100f) {
                    dismiss()
                }
            }
        }
        return true
    }

    override fun showDialog(globalDialogBean: GlobalDialogBean) {
        binding.tvNotice.text = globalDialogBean.content
        show()
        if (globalDialogBean.isAutoDismiss) {
            dismissJob = scope.launch(Dispatchers.Default) {
                delay(3000L)
                dismiss()
            }
        }
    }

    override fun handleDialog(globalDialogBean: GlobalDialogBean): Boolean = true
}