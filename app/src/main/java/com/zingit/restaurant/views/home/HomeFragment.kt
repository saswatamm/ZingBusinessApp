package com.zingit.restaurant.views.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.dantsu.escposprinter.connection.DeviceConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.zingit.restaurant.R
import com.zingit.restaurant.databinding.FragmentHomeBinding
import com.zingit.restaurant.models.OrderItem
import com.zingit.restaurant.models.PaymentModel
import com.zingit.restaurant.utils.hideKeyboard
import com.zingit.restaurant.utils.printer.AsyncBluetoothEscPosPrint
import com.zingit.restaurant.utils.printer.AsyncEscPosPrint
import com.zingit.restaurant.utils.printer.AsyncEscPosPrinter
import com.zingit.restaurant.views.FirebaseNotificationActionActivity
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var firestore: FirebaseFirestore
    private val TAG = "HomeFragment"
    lateinit var query: Query
    lateinit var paymentModel: PaymentModel
    private val selectedDevice: BluetoothConnection? = null
    val PERMISSION_BLUETOOTH = 1
    val PERMISSION_BLUETOOTH_ADMIN = 2
    val PERMISSION_BLUETOOTH_CONNECT = 3
    val PERMISSION_BLUETOOTH_SCAN = 4
    var outletID : String?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermissions()

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        firestore = FirebaseFirestore.getInstance()
        binding.apply {
            firestore.collection("outletID").document("SPVBed0F3hFKB9hx3hZD").get().addOnSuccessListener {
                outletID = it.data?.get("outletID").toString()
            }
            Handler().postDelayed({
                query = firestore.collection("payment").whereEqualTo("outletID",outletID).whereEqualTo("statusCode",1)
                query.addSnapshotListener(object :EventListener<QuerySnapshot> {
                    override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                        Log.e(TAG, "onCreateView: ${value!!.documents}")
                        if (error != null) {
                            Log.e(TAG, "fetchUsersData: ${error.message}")
                            return
                        }
                        for (i in value!!.documentChanges) {
                            Log.e(TAG, "fetchUsersData: ${i.document.data}")
                            when(i.type){
                                DocumentChange.Type.ADDED -> {
                                    Toast.makeText(requireContext(), i.document.data.get("paymentOrderID").toString(), Toast.LENGTH_SHORT).show()
                                    Log.e(TAG, "onEvent: ${i.document.data}")
                                    paymentModel = i.document.toObject(PaymentModel::class.java)
                                    Log.e(TAG, "onEvent: ${paymentModel.orderItems.size}", )

                                    printBluetooth()





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
            }, 5000)
/*
            firestore.collection("payment").whereEqualTo("outletID","1cLAN8pKJcuyIML9g8Uz").whereEqualTo("statusCode",1).addSnapshotListener { value, error ->
                Log.e(TAG, "onCreateView: ${value!!.documents}")
                if (error != null) {
                    Log.e(TAG, "fetchUsersData: ${error.message}")
                    return@addSnapshotListener
                }
                for (i in value!!.documents) {
                    Log.e(TAG, "fetchUsersData: ${i.data}")
                }
            }
*/


            searchView.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    Log.d("checkDatas", "brfiore: ${p0.toString()}")
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    Log.d("checkDatas", "onTextChanged: ${p0.toString()}")
                }

                override fun afterTextChanged(p0: Editable?) {
                    if (p0.toString().trim().isNotEmpty()) {
                        firestore.collection("payment").get().addOnSuccessListener {
                            var count:String ?=null
                            for (document in it) {
                                Log.e(TAG, "${document.id} => ${document.data.get("orderNo")}")
                                if (p0.toString().trim().contains(document.data.get("orderNo").toString())){
                                    Log.e(TAG, "${document.id} => ${document.data.get("orderNo")}")
                                    count= document.id
                                    Toast.makeText(context, document.id, Toast.LENGTH_SHORT).show()
                                    view?.hideKeyboard()
                                    startActivity(Intent(context, FirebaseNotificationActionActivity::class.java).putExtra("id",count))
                                    binding.searchView.text.clear()
                                    break
                                }

                            }

                        }
                    }
                }
            })


        }



        return binding.root
    }

    fun printBluetooth() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.BLUETOOTH),
                PERMISSION_BLUETOOTH
            )
        } else if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.BLUETOOTH_ADMIN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.BLUETOOTH_ADMIN),
                HomeFragment().PERMISSION_BLUETOOTH_ADMIN
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                HomeFragment().PERMISSION_BLUETOOTH_CONNECT
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.BLUETOOTH_SCAN),
                HomeFragment().PERMISSION_BLUETOOTH_SCAN
            )
        } else {
            AsyncBluetoothEscPosPrint(
                requireActivity(),
                object : AsyncEscPosPrint.OnPrintFinished() {
                   override fun onError(asyncEscPosPrinter: AsyncEscPosPrinter?, codeException: Int) {
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

                    }
                }
            )
                .execute(this.getAsyncEscPosPrinter(selectedDevice))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
                HomeFragment().PERMISSION_BLUETOOTH, HomeFragment().PERMISSION_BLUETOOTH_ADMIN, HomeFragment().PERMISSION_BLUETOOTH_CONNECT, HomeFragment().PERMISSION_BLUETOOTH_SCAN -> printBluetooth()
            }
        }
    }

    private fun checkPermissions() {
        val permission1 =
            ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permission2 =
            ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.BLUETOOTH_SCAN)
        if (permission1 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                1
            )
        } else if (permission2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                1
            )
        }
    }



    fun getAsyncEscPosPrinter(printerConnection: DeviceConnection?): AsyncEscPosPrinter? {
        val format = SimpleDateFormat("'on' yyyy-MM-dd 'at' HH:mm:ss")
        val printer = AsyncEscPosPrinter(printerConnection, 203, 48f, 32)
        return printer.addTextToPrint(createPrintSlip(paymentModel))
    }
    fun createPrintSlip(payment: PaymentModel): String? {
        var slip = "[C]<font size='big'>          ZING</font>"
        //slip = "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, this.getApplicationContext().getResources().getDrawableForDensity(R.drawable.logo_orange, DisplayMetrics.DENSITY_MEDIUM))+"</img>\n";
        slip += "[L]\n"
        slip += "[L]<b>Order type : "
        slip += """
             ${"[R]<font size='big'>        " + payment.orderType}</font>
             
             """.trimIndent()
//        slip += "[L]<b>" + "Order ID : "
//        slip += """
//             ${
//            "[R]<font size='big'>        " + "#" + payment.paymentOrderID
//                .substring(payment.paymentOrderID.length - 4)
//        }</font>
//
//             """.trimIndent()

        slip += "[L]<b>Order No. : "
        slip += """
             ${"[R]<font size='big'>        " + payment.orderNo}</font>
             
             """.trimIndent()
        slip += "[L]<b>" + "Order From : "
        slip += """
             ${"[R]<font size='big'>        " + payment.userName.split(" ")[0]}</font>
             
             """.trimIndent()
        //slip += "[L]<font size='big'>" + Dataholder.printingPayment.orderType + "           #" + Dataholder.printingPayment.getPaymentOrderID().substring(Dataholder.printingPayment.getPaymentOrderID().length()-4) + "</font>\n";
        //slip += "[L]<font size='big'>Order from        " + Dataholder.printingPayment.getUserName().toUpperCase() + "</font>\n";
        //Add phone no here
        slip += "[C]<b>=============================================\n"
        for (i in 0 until payment.orderItems.size) {

            slip += "[L]<font size='big-4'>" + payment.orderItems.get(i).itemName + "</font>"
            slip += """
            ${
                "[R]<font size='big-4'>      X" + payment.orderItems.get(i).itemQuantity
            }</font>
            
            
            """.trimIndent()
        }
        slip += "[C]<b>=============================================\n"
        slip += """
             ${"[R]<font size='big-4'>       Total Amount: " + payment.basePrice}</font>
             
             """.trimIndent()
        return slip
    }


}