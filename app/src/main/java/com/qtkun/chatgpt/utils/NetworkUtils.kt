package com.qtkun.chatgpt.utils

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.core.content.ContextCompat.getSystemService
import com.qtkun.chatgpt.GPTApplication

fun isConnected() : Boolean{
    val connectivityManager = getSystemService(GPTApplication.INSTANCE.applicationContext, ConnectivityManager::class.java)
    val network : Network? = connectivityManager?.activeNetwork
    network?.let {
        val networkCapabilities : NetworkCapabilities? = connectivityManager.getNetworkCapabilities(it)
        networkCapabilities?.let { capabilities ->
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true
            }
        }
    }
    return false
}