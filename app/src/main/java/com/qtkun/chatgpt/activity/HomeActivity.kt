package com.qtkun.chatgpt.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.qtkun.chatgpt.R
import com.qtkun.chatgpt.base.BaseActivity
import com.qtkun.chatgpt.base.BaseViewModel
import com.qtkun.chatgpt.databinding.ActivityHomeBinding
import com.qtkun.chatgpt.ext.dp
import com.qtkun.chatgpt.ext.singleClick
import com.qtkun.chatgpt.service.FloatingButtonService
import com.qtkun.chatgpt.utils.GlobalDialogBean
import com.qtkun.chatgpt.utils.GlobalDialogManager
import com.qtkun.chatgpt.utils.GlobalDialogType
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity: BaseActivity<ActivityHomeBinding, BaseViewModel>() {
    companion object {
        private const val REQUEST_CODE = 0x99
    }

    private val floatingIntent by lazy(LazyThreadSafetyMode.NONE) {
        Intent(this@HomeActivity, FloatingButtonService::class.java)
    }

    override fun ActivityHomeBinding.initViewBinding() {
        if (!Settings.canDrawOverlays(this@HomeActivity)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, REQUEST_CODE)
        } else {
            startService(floatingIntent)
        }
        (tvWater.layoutParams as MarginLayoutParams).topMargin = UltimateBarX.getStatusBarHeight() + 16.dp
        tvWater.requestLayout()
        tvWater.singleClick {
            DepthDetectionActivity.start(this@HomeActivity, false)
        }
        tvTrack.singleClick {
            DepthDetectionActivity.start(this@HomeActivity, true)
        }
        tvSafe.singleClick {
            startActivity(Intent(this@HomeActivity, SafetyRatingActivity::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(floatingIntent)
    }

    override fun BaseViewModel.initViewModel() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                // 用户已经授予悬浮窗权限，可以启动悬浮窗口服务
                startService(Intent(this@HomeActivity, FloatingButtonService::class.java))
            } else {
                // 用户拒绝了悬浮窗权限，可以显示一条提示信息
                Toast.makeText(this, "需要悬浮窗权限以显示按钮", Toast.LENGTH_SHORT).show()
            }
        }
    }
}