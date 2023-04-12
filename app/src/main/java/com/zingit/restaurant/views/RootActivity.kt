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
import com.zingit.restaurant.service.InternetConnectivityBroadcastReceiver
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RootActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeMainBinding
    lateinit var navController: NavController
    lateinit var internetConnectivityReceiver: InternetConnectivityBroadcastReceiver
    private val TAG = "RootActivity"
    private val isInternetConnectivity = MutableLiveData<Boolean>()
    private val isBluetoothConnected = MutableLiveData<Boolean>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home_main)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)
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


}