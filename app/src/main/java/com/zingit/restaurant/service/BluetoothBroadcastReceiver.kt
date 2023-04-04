package com.zingit.restaurant.service

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData

class BluetoothBroadcastReceiver(private val isBluetoothConnected : MutableLiveData<Boolean>): BroadcastReceiver() {
    private  val TAG = "BluetoothBroadcast"
    override fun onReceive(p0: Context?, p1: Intent?) {
        val action = p1?.action
        Toast.makeText(p0, "Bluetooth Broadcast", Toast.LENGTH_SHORT).show()
        if(isBluetoothAvailable()){
            Log.e(TAG, "onReceive: ${isBluetoothAvailable()}", )
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
    fun isBluetoothAvailable(): Boolean {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled && bluetoothAdapter.state == BluetoothAdapter.STATE_ON
    }


}