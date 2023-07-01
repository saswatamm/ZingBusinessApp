package com.zingit.restaurant.views

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice

import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
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
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.google.firebase.firestore.*
import com.zingit.restaurant.R
import com.zingit.restaurant.databinding.ActivityHomeMainBinding
import com.zingit.restaurant.models.order.OrdersModel
import com.zingit.restaurant.service.InternetConnectivityBroadcastReceiver
import com.zingit.restaurant.utils.Utils
import com.zingit.restaurant.utils.printer.AsyncBluetoothEscPosPrint
import com.zingit.restaurant.utils.printer.AsyncEscPosPrint
import com.zingit.restaurant.utils.printer.AsyncEscPosPrinter
import dagger.hilt.android.AndroidEntryPoint
import java.lang.reflect.Method


@AndroidEntryPoint
class RootActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeMainBinding
    private var mConnectedDeviceName: String? = null
    private var backPressedTime: Long = 0
    lateinit var navController: NavController
    var destination_id = R.id.homeFragment
    lateinit var internetConnectivityReceiver: InternetConnectivityBroadcastReceiver
    private val TAG = "RootActivity"
    val isInternetConnectivity = MutableLiveData<Boolean>()
    val isBluetoothConnected = MutableLiveData<Boolean>()
    val PERMISSION_BLUETOOTH = 1
    val PERMISSION_BLUETOOTH_ADMIN = 2
    val PERMISSION_BLUETOOTH_CONNECT = 3
    val PERMISSION_BLUETOOTH_SCAN = 4
    var mediaPlayer: MediaPlayer? = null

    var firestore = FirebaseFirestore.getInstance()
    var uniqueOrders = HashSet<String>() //To print only unique orders
    lateinit var paymentModel: OrdersModel
    var selectedDevice: BluetoothConnection? = null


    @SuppressLint("MissingPermission", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home_main)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)


        binding.bottomNavigationView.apply {
            navController.let { navController ->
                NavigationUI.setupWithNavController(
                    this, navController
                )
                setOnItemSelectedListener { item ->
                    NavigationUI.onNavDestinationSelected(item, navController)
                    true
                }
                setOnItemReselectedListener {
                    Log.d("getItemIDs", "onCreate: ${it.itemId}")
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


            Handler().postDelayed({
//                var query = firestore.collection("payment")
//                    .whereEqualTo("outletID", Utils.getUserOutletId(this))
//                    .whereEqualTo("statusCode", 1)
                var query = firestore.collection("prod_order")
                    .whereEqualTo("restaurant.details.restaurant_id", Utils.getUserOutletId(this))
                    .whereEqualTo("zingDetails.status", "0")

                query.addSnapshotListener(object : EventListener<QuerySnapshot> {
                    override fun onEvent(
                        value: QuerySnapshot?, error: FirebaseFirestoreException?
                    ) {
                        Log.e(TAG, "onCreateView: ${value!!.documents}")
                        if (error != null) {
                            Log.e(TAG, "fetchUsersData: ${error.message}")
                            return
                        }
                        for (i in value!!.documentChanges) {
                            Log.e(TAG, "fetchUsersData: ${i.document.data}")
                            when (i.type) {
                                DocumentChange.Type.ADDED -> {
                                    if (!uniqueOrders.contains(i.document.data.get("order.details.orderID").toString())) {
                                        uniqueOrders.add(i.document.data.get("order.details.orderID").toString()) // Unique orders are added to prevent repetative printing
                                        paymentModel = i.document.toObject(OrdersModel::class.java)
                                        Log.e(TAG, "onEvent: ${paymentModel.orderItem?.details!!.size}")
                                        printBluetooth(paymentModel, i.document.id)
                                        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.incoming_order)
                                        mediaPlayer?.start()
                                    } else {
                                        Log.e(TAG, "eventPrinting: ${i.document.data.get("order.details.orderID").toString()}")
                                    }
                                }
                                DocumentChange.Type.MODIFIED -> {
                                    Log.e(TAG, "onEvent: ${i.document.data}")
                                }
                                DocumentChange.Type.REMOVED -> {
                                    Log.e(TAG, "onEvent: ${i.document.data}")
                                }
                            }
                        }
                    }
                })
            }, 5)

        }

    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(internetConnectivityReceiver)
    }

    override fun onBackPressed() {
        if (navController.currentDestination?.id == R.id.homeFragment) {
            if (backPressedTime + 2000 > System.currentTimeMillis()) {
                super.onBackPressed()
            } else {
                Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
            }
            backPressedTime = System.currentTimeMillis()
        } else {
            super.onBackPressed()
        }

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
        selectedDevice= BluetoothConnection(getConnectedDeviceName())
    }




    private fun getConnectedDeviceName(): BluetoothDevice? {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.BLUETOOTH), PERMISSION_BLUETOOTH
            )
        } else if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.BLUETOOTH_ADMIN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.BLUETOOTH_ADMIN), PERMISSION_BLUETOOTH_ADMIN
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(
                this, Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), PERMISSION_BLUETOOTH_CONNECT
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(
                this, Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.BLUETOOTH_SCAN), PERMISSION_BLUETOOTH_SCAN
            )
        } else {
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            val connectedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
            for (device in connectedDevices!!) {
                if (isConnected(device)) {
                    return device
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



     fun printBluetooth(ordersModel: OrdersModel, id: String) {
        AsyncBluetoothEscPosPrint(this, object : AsyncEscPosPrint.OnPrintFinished() {
            override fun onError(
                asyncEscPosPrinter: AsyncEscPosPrinter?, codeException: Int
            ) {
                Log.e(
                    "Async.OnPrintFinished",
                    "AsyncEscPosPrint.OnPrintFinished : An error occurred !"
                )
            }

            override fun onSuccess(asyncEscPosPrinter: AsyncEscPosPrinter?) {
                Log.i(
                    "Async.OnPrintFinished",
                    "AsyncEscPosPrint.OnPrintFinished : Print is finished !"
                )

                try {

                    val sfDocRef = firestore.collection("payment").document(id)
                    Toast.makeText(
                        applicationContext, "Print is finished ! $id", Toast.LENGTH_SHORT
                    ).show()

                    firestore.runTransaction { transaction ->
                        transaction.update(sfDocRef, "statusCode", 2)
                    }.addOnSuccessListener {
                        Log.d(TAG, "Transaction success!")

                    }.addOnFailureListener { e ->
                        Toast.makeText(
                            applicationContext, "Error $e", Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(applicationContext, "Error $e", Toast.LENGTH_LONG).show()
                }
            }

        }).execute(
            Utils.getAsyncEscPosPrinter(
                ordersModel, selectedDevice, this@RootActivity
            )
        )
    }



}








