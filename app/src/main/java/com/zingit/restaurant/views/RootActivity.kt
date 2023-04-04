package com.zingit.restaurant.views

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.zingit.restaurant.R
import com.zingit.restaurant.databinding.ActivityHomeMainBinding
import com.zingit.restaurant.service.BluetoothBroadcastReceiver
import com.zingit.restaurant.service.InternetConnectivityBroadcastReceiver

class RootActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeMainBinding
    lateinit var navController: NavController
    lateinit var internetConnectivityReceiver: InternetConnectivityBroadcastReceiver
    lateinit var isBluetoothBroadcastReceiver: BluetoothBroadcastReceiver
    private val TAG = "RootActivity"
    private val isInternetConnectivity = MutableLiveData<Boolean>()
    private val isBluetoothConnected = MutableLiveData<Boolean>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home_main)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)
        isInternetConnectivity.observe(this@RootActivity) {
            if (it) {
                binding.fragmentContainerView.visibility = View.VISIBLE
                binding.noInternet.root.visibility = View.GONE
            } else {
                binding.fragmentContainerView.visibility = View.GONE
                binding.noInternet.root.visibility = View.VISIBLE
                binding.noInternet.getStarted.setOnClickListener {
                    startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                }
            }

        }
        isBluetoothConnected.observe(this@RootActivity) {
            if (it) {
                binding.fragmentContainerView.visibility = View.VISIBLE
                binding.noBluetooth.root.visibility = View.GONE
            } else {
                binding.fragmentContainerView.visibility = View.GONE
                binding.noBluetooth.root.visibility = View.VISIBLE
                binding.noBluetooth.getStarted.setOnClickListener{
                    startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
                }

            }
        }


    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(internetConnectivityReceiver)
        unregisterReceiver(isBluetoothBroadcastReceiver)
    }




    override fun onStart() {
        super.onStart()
        isBluetoothBroadcastReceiver = BluetoothBroadcastReceiver(isBluetoothConnected)
        internetConnectivityReceiver = InternetConnectivityBroadcastReceiver(isInternetConnectivity)
        registerReceiver(internetConnectivityReceiver, IntentFilter().apply {
            addAction("android.net.conn.CONNECTIVITY_CHANGE")
        })

        registerReceiver(isBluetoothBroadcastReceiver, IntentFilter().apply {
            addAction("android.bluetooth.device.action.ACL_CONNECTED")
            addAction("android.bluetooth.device.action.ACL_DISCONNECTED")
            addAction("android.bluetooth.adapter.action.STATE_CHANGED")
        })
    }


}