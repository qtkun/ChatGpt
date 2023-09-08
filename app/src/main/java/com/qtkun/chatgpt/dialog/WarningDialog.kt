package com.qtkun.chatgpt.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.method.ScrollingMovementMethod
import android.view.Gravity
import android.view.WindowManager
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatDialog
import androidx.core.view.isVisible
import com.qtkun.chatgpt.R
import com.qtkun.chatgpt.databinding.DialogWarningBinding
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
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch

class WarningDialog @JvmOverloads constructor(
    context: Context,
    @StyleRes themeResId: Int = R.style.CustomDialog
): AppCompatDialog(context, themeResId), IGlobalDialog {
    private val binding by viewBinding<DialogWarningBinding>()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate + CoroutineName("WarningDialog"))
    private var dismissJob: Job? = null

    init {
        binding.tvConfirm.setOnClickListener {
            dismiss()
        }
        binding.tvContent.movementMethod = ScrollingMovementMethod.getInstance()
        setOnDismissListener {
            if (dismissJob?.isActive == true) {
                dismissJob?.cancel()
                dismissJob = null
            }
            GlobalDialogManager.instance.dismissDialog()
        }
        setCanceledOnTouchOutside(true)
        window?.run {
            setDimAmount(0.2f)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.CENTER)
        }
    }

    override fun showDialog(globalDialogBean: GlobalDialogBean) {
        binding.tvTitle.isVisible = !globalDialogBean.title.isNullOrEmpty()
        binding.tvTitle.text = globalDialogBean.title
        binding.tvContent.text = globalDialogBean.content
        globalDialogBean.icon?.let {
            binding.tvTitle.setCompoundDrawablesWithIntrinsicBounds(it, 0, 0, 0)
        }
        show()
        if (globalDialogBean.isAutoDismiss) {
            dismissJob = scope.launch(Dispatchers.Default) {
                delay(4000L)
                dismiss()
            }
        }
    }

    override fun handleDialog(globalDialogBean: GlobalDialogBean): Boolean = true

}