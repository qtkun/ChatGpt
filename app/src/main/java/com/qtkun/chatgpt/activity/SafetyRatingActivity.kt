package com.qtkun.chatgpt.activity

import androidx.lifecycle.lifecycleScope
import com.qtkun.chatgpt.R
import com.qtkun.chatgpt.base.BaseActivity
import com.qtkun.chatgpt.base.BaseViewModel
import com.qtkun.chatgpt.databinding.ActivitySafetyRatingBinding
import com.qtkun.chatgpt.ext.singleClick
import com.qtkun.chatgpt.utils.GlobalDialogBean
import com.qtkun.chatgpt.utils.GlobalDialogManager
import com.qtkun.chatgpt.utils.GlobalDialogType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SafetyRatingActivity: BaseActivity<ActivitySafetyRatingBinding, BaseViewModel>() {
    override fun ActivitySafetyRatingBinding.initViewBinding() {
        ivBack.singleClick { finish() }
        lifecycleScope.launch {
            delay(2000L)
            GlobalDialogManager.instance.sendDialogRequest(
                GlobalDialogBean(
                    GlobalDialogType.WARNING,
                    "检查提醒",
                    "车辆内部件可能收到损伤，为了保证您的安全，请及时到4S店进行车辆检查！",
                    R.drawable.ic_filled
                )
            )
        }
    }

    override fun BaseViewModel.initViewModel() {}
}