package com.zingit.restaurant.service

interface Constants {
    companion object {

        // Message types sent from the BluetoothService Handler
        val DEVICE_SELECTED = 1
        val MESSAGE_TOAST = 2

        // Key names received from the BluetoothService Handler
        val DEVICE_NAME = "device_name"
        val TOAST = "toast"
    }

}