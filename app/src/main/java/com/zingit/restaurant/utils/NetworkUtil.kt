package com.zingit.restaurant.utils

import android.content.Context
import android.net.ConnectivityManager


object NetworkUtil {
    const val TYPE_WIFI = 1
    const val TYPE_MOBILE = 2
    const val TYPE_NOT_CONNECTED = 0
    const val NETWORK_STATUS_NOT_CONNECTED = 0
    const val NETWORK_STATUS_WIFI = 1
    const val NETWORK_STATUS_MOBILE = 2
    fun getConnectivityStatus(context: Context): Int {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        if (null != activeNetwork) {
            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) return TYPE_WIFI
            if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) return TYPE_MOBILE
        }
        return TYPE_NOT_CONNECTED
    }

    fun getConnectivityStatusString(context: Context): Boolean {
        val conn = getConnectivityStatus(context)
        var status = true
        if (conn == TYPE_WIFI) {
            status = true
        } else if (conn == TYPE_MOBILE) {
            status = true
        } else if (conn == TYPE_NOT_CONNECTED) {
            status = false
        }
        return status
    }
}