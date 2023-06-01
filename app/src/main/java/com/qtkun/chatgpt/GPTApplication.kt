package com.qtkun.chatgpt

import android.app.ActivityManager
import android.app.Application
import android.os.Process
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GPTApplication: Application() {
    companion object {
        lateinit var INSTANCE: GPTApplication
    }

    private val isMainProcess: Boolean
        get() {
            val pid = Process.myPid()
            val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
            for (appProcess in activityManager.runningAppProcesses) {
                if (appProcess.pid == pid) {
                    return applicationInfo.packageName == appProcess.processName
                }
            }
            return false
        }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }
}