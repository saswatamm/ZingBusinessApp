package com.zingit.restaurant.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.zingit.restaurant.utils.NetworkUtil

class InternetConnectivityBroadcastReceiver(private val isInternetConnected :MutableLiveData<Boolean>):BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        val status = NetworkUtil.getConnectivityStatusString(p0!!)
        if(status){
            isInternetConnected.postValue(true)
        }else{
            isInternetConnected.postValue(false)
        }
    }
}