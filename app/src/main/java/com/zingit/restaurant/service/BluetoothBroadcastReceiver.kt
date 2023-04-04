package com.zingit.restaurant.service

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData

class BluetoothBroadcastReceiver(private val isBluetoothConnected : MutableLiveData<Boolean>): BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        val action = p1?.action
        val bluetoothAdapter =  BluetoothAdapter.getDefaultAdapter()
        if(bluetoothAdapter.isEnabled){
            isBluetoothConnected.value = true
        }else{
            isBluetoothConnected.value = false
        }
        if (BluetoothDevice.ACTION_ACL_CONNECTED == action) {
            isBluetoothConnected.value = true

        } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED == action) {
            // Bluetooth device is disconnected
            isBluetoothConnected.value = false
        }


    }


}