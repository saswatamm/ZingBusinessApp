package com.zingit.restaurant.service

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.zingit.restaurant.utils.NetworkUtil

class InternetConnectivityBroadcastReceiver(
    private val isInternetConnected: MutableLiveData<Boolean>,
    private val isBluetoothConnected: MutableLiveData<Boolean>
) :
    BroadcastReceiver() {
    private  val TAG = "BroadcastReciever"
    override fun onReceive(p0: Context?, p1: Intent?) {
        val status = NetworkUtil.getConnectivityStatusString(p0!!)
        if (status) {
            isInternetConnected.postValue(true)
        } else {
            isInternetConnected.postValue(false)
        }

        val action = p1?.action

//        isBluetoothConnected.value = true
//        if (isBluetoothAvailable()) {
//            Log.e(TAG, "onReceive: ${isBluetoothAvailable()}")
//           // isBluetoothConnected.value = true
//        } else {
//            //isBluetoothConnected.value = false
//        }
        if (BluetoothDevice.ACTION_ACL_CONNECTED == action) {
            //isBluetoothConnected.value = true

        } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED == action) {
            // Bluetooth device is disconnected
            //isBluetoothConnected.value = false
        }


    }

    @SuppressLint("MissingPermission")
    fun isBluetoothAvailable(): Boolean {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val bondedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices


        return bluetoothAdapter != null && bluetoothAdapter.isEnabled && bluetoothAdapter.state == BluetoothAdapter.STATE_ON

    }


}