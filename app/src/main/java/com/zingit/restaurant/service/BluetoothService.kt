package com.zingit.restaurant.service

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.lang.Integer.TYPE
import java.util.*


class BluetoothService(context: Context, private val mHandler: Handler) {

    // Member fields
    private val context: Context = context
    private val mAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    public var currentConnectedDevice: BluetoothDevice? = null

    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume()
     */
    @Synchronized
    fun start() {
        Log.d(TAG, "start")
    }

    @Synchronized
    fun selectDevice(device: BluetoothDevice) {
        currentConnectedDevice = device
        deviceSelected(device)
    }


    @Synchronized
    private fun sendMessage(device: BluetoothDevice, secure: Boolean, message: String) {
        Log.d(TAG, "connect to: " + device)

        // Start the thread to connect with the given device
        MessageThread(device, secure, message).start()
    }

    @Synchronized
    fun sendMessageToCurrentDevice(message: String) {
        val device = currentConnectedDevice
        if (device != null) {
            sendMessage(device, true, message)
        } else {
            connectionFailed()
        }
    }

    @Synchronized
    fun deviceSelected(device: BluetoothDevice) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        Log.d(TAG, "selected device ${device.name}")

        // Send the name of the connected device back to the UI Activity
        val msg = mHandler.obtainMessage(Constants.DEVICE_SELECTED)
        val bundle = Bundle()
        bundle.putString(Constants.DEVICE_NAME, device.name)
        msg.data = bundle
        mHandler.sendMessage(msg)
    }

    @Synchronized
    fun messageSent(device: BluetoothDevice, socketType: String, message: String) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        Log.d(TAG, "sent message, Socket Type: $socketType to ${device.name}")

        // Send the name of the connected device back to the UI Activity
        val msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST)
        val bundle = Bundle()
        bundle.putString(Constants.TOAST, message)
        msg.data = bundle
        mHandler.sendMessage(msg)
    }

    /**
     * Stop all threads
     */
    @Synchronized
    fun stop() {
        Log.d(TAG, "stop")
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private fun connectionFailed() {
        // Send a failure message back to the Activity
        val msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST)
        val bundle = Bundle()
        bundle.putString(Constants.TOAST, "Unable to connect device")
        msg.data = bundle
        mHandler.sendMessage(msg)

        // Start the service over to restart listening mode
        this@BluetoothService.start()
    }

    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device if succeed then send message and closes socket.
     */
    private inner class MessageThread(
        private val mmDevice: BluetoothDevice,
        private val secure: Boolean,
        private val message: String
    ) : Thread() {
        private val mSocketType: String
            get() = if (secure) "Secure" else "Insecure"

        override fun run() {
            Log.i(TAG, "BEGIN mConnectThread SocketType:" + mSocketType)
            name = "ConnectThread" + mSocketType

            var bluetoothSocket: BluetoothSocket? = null;
            // Make a connection to the BluetoothSocket
            try {
                bluetoothSocket = if (secure) {
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return
                    }
                    mmDevice.createRfcommSocketToServiceRecord(
                        MY_UUID_SECURE
                    )
                } else {
                    mmDevice.createInsecureRfcommSocketToServiceRecord(
                        MY_UUID_INSECURE
                    )
                }
                // Always cancel discovery because it will slow down a connection
                mAdapter.cancelDiscovery()
                // This is a blocking call and will only return on a
                // successful connection or an exception
                try {
                    bluetoothSocket.connect()
                } catch (e: Exception) {
                    //Fallback of BT issue
                    //See http://stackoverflow.com/questions/18657427/ioexception-read-failed-socket-might-closed-bluetooth-on-android-4-3
                    Log.w(TAG, e.message, e)
                    Log.e(TAG, "trying fallback...")

                    bluetoothSocket = mmDevice.javaClass.getMethod(
                        if (secure) {
                            "createRfcommSocket"
                        } else {
                            "createInsecureRfcommSocket"
                        }, *arrayOf<Class<Int>>(TYPE)
                    ).invoke(mmDevice, 1) as BluetoothSocket
                    bluetoothSocket.connect()

                    Log.i(TAG, "Connected via fallback")
                }
            } catch (e: IOException) {
                // Close the socket
                try {
                    bluetoothSocket?.close()
                } catch (e2: IOException) {
                    Log.e(
                        TAG,
                        "unable to close() $mSocketType socket during connection failure",
                        e2
                    )
                }

                connectionFailed()
                return
            }

            try {
                bluetoothSocket.outputStream.write(message.toByteArray())
                bluetoothSocket.close();
            } catch (e: IOException) {
                Log.e(TAG, "close() of connect $mSocketType socket failed", e)
            }

            messageSent(mmDevice, mSocketType, message)
        }

    }

    companion object {
        // Debugging
        private val TAG = "BluetoothService"

        // Unique UUID for this application
        private val MY_UUID_SECURE = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
        private val MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")

        // Constants that indicate the current connection state
        val STATE_NONE = 0       // we're doing nothing
        val STATE_LISTEN = 1     // now listening for incoming connections
        val STATE_CONNECTING = 2 // now initiating an outgoing connection
        val STATE_CONNECTED = 3  // now connected to a remote device
    }
}