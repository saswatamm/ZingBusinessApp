package com.zingit.restaurant.views

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.zingit.restaurant.R
import com.zingit.restaurant.databinding.ActivityHomeMainBinding
import com.zingit.restaurant.service.InternetConnectivityBroadcastReceiver
import dagger.hilt.android.AndroidEntryPoint
import java.lang.reflect.Method


@AndroidEntryPoint
class RootActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeMainBinding
    lateinit var navController: NavController
    lateinit var internetConnectivityReceiver: InternetConnectivityBroadcastReceiver
    private val TAG = "RootActivity"
     val isInternetConnectivity = MutableLiveData<Boolean>()
     val isBluetoothConnected = MutableLiveData<Boolean>()
    val PERMISSION_BLUETOOTH = 1
    val PERMISSION_BLUETOOTH_ADMIN = 2
    val PERMISSION_BLUETOOTH_CONNECT = 3
    val PERMISSION_BLUETOOTH_SCAN = 4
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home_main)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)
        Toast.makeText(this, "Device ${getConnectedDeviceName()}", Toast.LENGTH_SHORT).show()


        binding.bottomNavigationView.apply {
            navController.let { navController ->
                NavigationUI.setupWithNavController(
                    this,
                    navController
                )
                setOnItemSelectedListener { item ->
                    NavigationUI.onNavDestinationSelected(item, navController)
                    true
                }
                setOnItemReselectedListener {
                    navController.popBackStack(destinationId = it.itemId, inclusive = false)
                }
            }
        }
        isInternetConnectivity.observe(this@RootActivity) { inter ->
            isBluetoothConnected.observe(this@RootActivity) { blue ->

                if (inter && blue) {
                    binding.fragmentContainerView.visibility = View.VISIBLE
                    binding.noInternet.root.visibility = View.GONE
                    binding.noBluetooth.root.visibility = View.GONE
                    binding.combined.root.visibility = View.GONE
                } else if (inter && !blue) {
                    binding.fragmentContainerView.visibility = View.GONE
                    binding.noInternet.root.visibility = View.GONE
                    binding.noBluetooth.root.visibility = View.VISIBLE
                    binding.combined.root.visibility = View.GONE
                    binding.noBluetooth.getStarted.setOnClickListener {
                        startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
                    }
                } else if (!inter && blue) {
                    binding.fragmentContainerView.visibility = View.GONE
                    binding.noInternet.root.visibility = View.VISIBLE
                    binding.noBluetooth.root.visibility = View.GONE
                    binding.combined.root.visibility = View.GONE
                    binding.noInternet.getStarted.setOnClickListener {
                        startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                    }
                } else {
                    binding.fragmentContainerView.visibility = View.GONE
                    binding.noInternet.root.visibility = View.GONE
                    binding.noBluetooth.root.visibility = View.GONE
                    binding.combined.root.visibility = View.VISIBLE
                    binding.combined.getStarted.setOnClickListener {
                        startActivity(Intent(Settings.ACTION_SETTINGS))
                    }

                }

            }

        }

    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(internetConnectivityReceiver)
    }


    override fun onStart() {
        super.onStart()
        internetConnectivityReceiver =
            InternetConnectivityBroadcastReceiver(isInternetConnectivity, isBluetoothConnected)
        registerReceiver(internetConnectivityReceiver, IntentFilter().apply {
            addAction("android.net.conn.CONNECTIVITY_CHANGE")
            addAction("android.bluetooth.device.action.ACL_CONNECTED")
            addAction("android.bluetooth.device.action.ACL_DISCONNECTED")
            addAction("android.bluetooth.adapter.action.STATE_CHANGED")
        })


    }


    private fun getConnectedDeviceName(): String? {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
               this,
                arrayOf(Manifest.permission.BLUETOOTH),
                PERMISSION_BLUETOOTH
            )
        } else if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_ADMIN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
               this,
                arrayOf(Manifest.permission.BLUETOOTH_ADMIN), PERMISSION_BLUETOOTH_ADMIN
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(
               this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
               this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                PERMISSION_BLUETOOTH_CONNECT
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(
               this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
               this,
                arrayOf(Manifest.permission.BLUETOOTH_SCAN),
                PERMISSION_BLUETOOTH_SCAN
            )
        }else{
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            val connectedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
            for (device in connectedDevices!!) {
                if (isConnected(device)) {
                    return device.name
                }
            }
        }




        return null
    }

    private fun isConnected(device: BluetoothDevice): Boolean {
        return try {
            val m: Method = device.javaClass.getMethod("isConnected")
            m.invoke(device) as Boolean
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }


}