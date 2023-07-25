package com.zingit.restaurant.views

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.util.Log
import android.view.View
import android.webkit.PermissionRequest
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

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
    val PERMISSION_BLUETOOTH_ADVERTISE = 5
    var mediaPlayer: MediaPlayer? = null

    var firestore = FirebaseFirestore.getInstance()
    var uniqueOrders = HashSet<String>() //To print only unique orders
    lateinit var paymentModel: OrdersModel
    var selectedDevice: BluetoothConnection? = null

    var context: Context = this;



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
                Dexter.withContext(this)
                    .withPermissions(
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADVERTISE,
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT,

                    ).withListener(object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(report: MultiplePermissionsReport) {

                        }

                        override fun onPermissionRationaleShouldBeShown(
                            p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                            p1: PermissionToken?
                        ) {
                            TODO("Not yet implemented")
                        }

                    }).check()

                // Get the BluetoothDevice instance
                val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                if(Utils.getPrinterMac(this@RootActivity) != null){
                    val device: BluetoothDevice? = bluetoothAdapter?.getRemoteDevice(Utils.getPrinterMac(this@RootActivity))
                    // Bond with the device
                    device?.createBond()
                    // Check if the device is already bonded
                    val isBonded: Boolean = device?.bondState == BluetoothDevice.BOND_BONDED
                    if (isBonded) {
                        Log.d("BONDED", "Device Bonded");
                    } else {
                        Log.d("BONDED", "Device Not Bonded");
                    }
                }else{
                    Toast.makeText(this@RootActivity, "Mac not found in db", Toast.LENGTH_SHORT).show()
                }

            }


            Handler().postDelayed({

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
                                    paymentModel = i.document.toObject(OrdersModel::class.java)
                                    paymentModel.zingDetails?.id = i.document.id
                                    if(!uniqueOrders.contains(i.document.id)){
                                        uniqueOrders.add(i.document.id)
                                        Log.d("RootActivity", "InPrinting should happen")
                                        Log.e(TAG, "onEvent: ${paymentModel.orderItem?.details!!.size}")
                                        try{
                                            var customerDetails = paymentModel.customer?.details!!.phone;
                                            if(customerDetails.length>9)
                                            {
                                                printBluetooth(context,this@RootActivity,paymentModel, i.document.id)
                                                mediaPlayer = MediaPlayer.create(this@RootActivity, R.raw.incoming_order)
                                                mediaPlayer?.start()
                                            }
                                        }
                                        catch (e: Exception){

                                        }

                                    }
                                    else {
                                        Log.d("RootActivity", "Printing should happen")
                                        Log.e(TAG, "eventPrintingRoot: ${i.document.data.get("order.details.orderID").toString()}")
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
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(
                this, Manifest.permission.BLUETOOTH_ADVERTISE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.BLUETOOTH_ADVERTISE), PERMISSION_BLUETOOTH_ADVERTISE
            )
        }
        else {
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            val connectedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
            for (device in connectedDevices!!) {

                if (isConnected(device)) {
                    Log.e("Connected Device : ", device.name);
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

    fun printBluetooth(context: Context,activity: Activity,ordersModel: OrdersModel, id: String) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.BLUETOOTH),
                PERMISSION_BLUETOOTH
            )
        } else if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_ADMIN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.BLUETOOTH_ADMIN), PERMISSION_BLUETOOTH_ADMIN
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                PERMISSION_BLUETOOTH_CONNECT
            )
        } else {
            AsyncBluetoothEscPosPrint(
                context,
                object : AsyncEscPosPrint.OnPrintFinished() {
                    override fun onError(
                        asyncEscPosPrinter: AsyncEscPosPrinter?,
                        codeException: Int
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
                            val sfDocRef = firestore.collection("prod_order").document(id)
//                            Toast.makeText(
//                                context,
//                                "Print is finished in OrdersFragment ! $id",
//                                Toast.LENGTH_SHORT
//                            ).show()
                            Log.d(TAG, "Print is finished in OrdersFragment !$id")
                            firestore.runTransaction { transaction ->
                                transaction.update(sfDocRef, "zingDetails.status", "2")
                            }.addOnSuccessListener {
                                Log.d(TAG, "Transaction success!")

                            }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        context,
                                        "Error $e",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error $e", Toast.LENGTH_LONG).show()
                        }
                    }

                }
            )
                .execute(Utils.getAsyncEscPosPrinter(ordersModel, selectedDevice, context))
        }
    }

    private val bondStateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED == action) {
                val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                val bondState: Int? = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR)

                // Handle bond state changes
                when (bondState) {
                    BluetoothDevice.BOND_BONDING -> {
                        // Device is currently bonding
                    }
                    BluetoothDevice.BOND_BONDED -> {
                        // Device has been successfully bonded
                    }
                    BluetoothDevice.BOND_NONE -> {
                        // Bonding with the device has been cancelled or failed
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // Register the BroadcastReceiver
        val filter = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        registerReceiver(bondStateReceiver, filter)
    }

    override fun onPause() {
        super.onPause()

        // Unregister the BroadcastReceiver
        unregisterReceiver(bondStateReceiver)
    }















//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        Log.e("hehe22", grantResults[0].toString())
//        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            makeDiscoverable()
//            when (requestCode) {
//                PERMISSION_BLUETOOTH, PERMISSION_BLUETOOTH_ADMIN, PERMISSION_BLUETOOTH_CONNECT, PERMISSION_BLUETOOTH_SCAN, PERMISSION_BLUETOOTH_ADVERTISE -> {
//                    if(requestCode==PERMISSION_BLUETOOTH_ADVERTISE){
//                        //makeDiscoverable()
//                    }
//                }
//            }
//        }
//    }
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == 0) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
//
//            } else {
//                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }



//     fun printBluetooth(activity: Activity, ordersModel: OrdersModel, id: String) {
//        AsyncBluetoothEscPosPrint(this, object : AsyncEscPosPrint.OnPrintFinished() {
//            override fun onError(
//                asyncEscPosPrinter: AsyncEscPosPrinter?, codeException: Int
//            ) {
//                Log.e(
//                    "Async.OnPrintFinished",
//                    "AsyncEscPosPrint.OnPrintFinished : An error occurred !"
//                )
//            }
//
//            override fun onSuccess(asyncEscPosPrinter: AsyncEscPosPrinter?) {
//                Log.i(
//                    "Async.OnPrintFinished",
//                    "AsyncEscPosPrint.OnPrintFinished : Print is finished !"
//                )
//
//                try {
//
//                    val sfDocRef = firestore.collection("prod_order").document(id)
//                    Toast.makeText(
//                        activity, "Print is finished ! $id", Toast.LENGTH_SHORT
//                    ).show()
//
//                    firestore.runTransaction { transaction ->
//                        transaction.update(sfDocRef, "zingDetails.status", "2")
//                    }.addOnSuccessListener {
//                        Log.d(TAG, "Transaction success!")
//
//                    }.addOnFailureListener { e ->
//                        Toast.makeText(
//                            activity, "Error $e", Toast.LENGTH_LONG
//                        ).show()
//                    }
//                } catch (e: Exception) {
//                    Toast.makeText(activity, "Error $e", Toast.LENGTH_LONG).show()
//                }
//            }
//
//        }).execute(
//            Utils.getAsyncEscPosPrinter(
//                ordersModel, selectedDevice, activity
//            )
//        )
//    }



}








