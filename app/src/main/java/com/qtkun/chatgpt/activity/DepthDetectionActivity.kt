package com.qtkun.chatgpt.activity

import android.content.Context
import android.content.Intent
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.qtkun.chatgpt.R
import com.qtkun.chatgpt.base.BaseActivity
import com.qtkun.chatgpt.base.BaseViewModel
import com.qtkun.chatgpt.databinding.ActivityDepthDetectionBinding
import com.qtkun.chatgpt.ext.singleClick
import com.qtkun.chatgpt.utils.GlobalDialogBean
import com.qtkun.chatgpt.utils.GlobalDialogManager
import com.qtkun.chatgpt.utils.GlobalDialogType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DepthDetectionActivity: BaseActivity<ActivityDepthDetectionBinding, BaseViewModel>() {
    companion object {
        private const val SHOW_MAP = "show_map"

        fun start(context: Context, showing: Boolean) {
            context.startActivity(Intent(context, DepthDetectionActivity::class.java).apply {
                putExtra(SHOW_MAP, showing)
            })
        }
    }
    private val depthList = listOf(
        DepthBean(0, 10, 0),
        DepthBean(5, 10, 5),
        DepthBean(6, 13, 7),
        DepthBean(7, 14, 8),
        DepthBean(10, 20, 10),
        DepthBean(16, 25, 18),
        DepthBean(20, 28, 21),
        DepthBean(18, 36, 23),
        DepthBean(25, 40, 30),
        DepthBean(30, 37, 28),
        DepthBean(28, 34, 25),
        DepthBean(24, 29, 26),
        DepthBean(25, 31, 30),
        DepthBean(32, 33, 29),
        DepthBean(35, 38, 35),
        DepthBean(39, 42, 40),
        DepthBean(42, 45, 42),
        DepthBean(45, 44, 43),
        DepthBean(45, 46, 46),
        DepthBean(47, 50, 48),
    )

    private var isMapShowing = false

    override fun ActivityDepthDetectionBinding.initViewBinding() {
        showMap(intent.getBooleanExtra(SHOW_MAP, false))
        ivBack.singleClick { finish() }
        tvTrack.singleClick {
            showMap(!isMapShowing)
        }
        lifecycleScope.launch {
            delay(2000L)
            GlobalDialogManager.instance.sendDialogRequest(GlobalDialogBean(
                GlobalDialogType.NOTICE,
                "暴风雨警告",
                "未来一小时内可能有暴风雨，请车主谨慎驾驶！及时打开雨刮器、前照灯、示廓灯和后尾灯，尽快将车辆驶离路段或定在安全地方。",
                null
            ))
            delay(3000L)
            (0..Int.MAX_VALUE).asFlow()
                .collect {
                    val depth1 = depthList[it % 10 * 2]
                    val depth2 = depthList[it % 10 * 2 + 1]
                    tvLeft1.text = "左前水深：${depth1.left}cm"
                    tvFace1.text = "左前水深：${depth1.face}cm"
                    tvRight1.text = "右前水深：${depth1.right}cm"
                    tvLeft2.text = "左前水深：${depth2.left}cm"
                    tvFace2.text = "左前水深：${depth2.face}cm"
                    tvRight2.text = "右前水深：${depth2.right}cm"
                    if (it == 9) {
                        GlobalDialogManager.instance.sendDialogRequest(GlobalDialogBean(
                            GlobalDialogType.WARNING,
                            "水深警告",
                            "车辆当前处于水深位置，水深已达50cm，可能右渗水风险，请尽快小心行驶到安全地段！",
                            null
                        ))
                    }
                    delay(2000L)
                }
        }
        lifecycleScope.launch {
            delay(10000L)
            GlobalDialogManager.instance.sendDialogRequest(GlobalDialogBean(
                GlobalDialogType.WARNING,
                "障碍物警告",
                "车辆正前方2米有障碍物，请小心驾驶，注意躲避！",
                R.drawable.ic_obstacle
            ))
        }
    }

    private fun ActivityDepthDetectionBinding.showMap(showing: Boolean) {
        isMapShowing = showing
        ivMap.isVisible = isMapShowing
        lottie.isVisible = isMapShowing
        tvTrack.text = if (isMapShowing) "关闭洪流轨迹预测" else "洪流轨迹预测"
        if (isMapShowing) {
            lottie.playAnimation()
        } else {
            lottie.cancelAnimation()
        }
    }

    override fun BaseViewModel.initViewModel() {

    }
}

data class DepthBean(
    val left: Int,
    val face: Int,
    val right: Int
)