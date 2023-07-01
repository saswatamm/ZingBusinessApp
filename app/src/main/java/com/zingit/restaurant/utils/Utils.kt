package com.zingit.restaurant.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import com.dantsu.escposprinter.connection.DeviceConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.google.android.gms.common.api.Api.Client
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.zingit.restaurant.R
import com.zingit.restaurant.models.order.OrderItem
import com.zingit.restaurant.models.order.OrderItemDetails
import com.zingit.restaurant.models.order.OrdersModel
import com.zingit.restaurant.utils.printer.AsyncBluetoothEscPosPrint
import com.zingit.restaurant.utils.printer.AsyncEscPosPrint
import com.zingit.restaurant.utils.printer.AsyncEscPosPrinter
import com.zingit.restaurant.views.order.NewOrderFragment
import com.zingit.restaurant.views.order.NewOrderFragment.Companion.PERMISSION_BLUETOOTH
import com.zingit.restaurant.views.order.NewOrderFragment.Companion.PERMISSION_BLUETOOTH_SCAN
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


object Utils {

    private const val TAG = "Utils"


    fun checkContactNumber(contact: String): Boolean {
        val expression = "^[6-9]\\d{9}$"
        val inputStr: CharSequence = contact
        val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(inputStr)
        return matcher.matches()
    }

    fun Fragment.setInteractionDisabled(disabled: Boolean) {
        if (disabled) {
            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        } else {
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertToIsoString(date: Date): String {
        // Convert the Timestamp object to a Date object

        // Create a SimpleDateFormat to format the Date object
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        // Format the Date object into a string in the desired format
        return formatter.format(date)
    }
  @RequiresApi(Build.VERSION_CODES.O)
    fun convertToIsoString1(date: String?): String {
        // Convert the Timestamp object to a Date object

        // Create a SimpleDateFormat to format the Date object
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        // Format the Date object into a string in the desired format
        return formatter.format(date)
    }

    fun View.hideKeyboard() {
        val inputMethodManager =
            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(this.windowToken, 0)
    }

    fun insertUserInfo(
        context: Context,
        account_id: String,
        email: String,
        outlet_id: String,
        menu_sharing_code:String,
    ) {
        val sharedPreference = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putString("account_id", account_id)
        editor.putString("email", email)
        editor.putString("outlet_id", outlet_id)
        editor.putString("menu_sharing_code", menu_sharing_code)
        editor.commit()
    }


    fun getUserInfo(context: Context): String? {
        val sharedPreference = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        return sharedPreference.getString("account_id", null)
    }

    fun getMenuSharingCode(context: Context): String? {
        val sharedPreference = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        return sharedPreference.getString("menu_sharing_code", null)
    }

    fun getUserEmail(context: Context): String? {
        val sharedPreference = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        return sharedPreference.getString("email", null)
    }

    fun getUserOutletId(context: Context): String? {
        val sharedPreference = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        return sharedPreference.getString("outlet_id", null)
    }

    fun deleteUserInfo(context: Context) {
        val sharedPreference = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        sharedPreference.edit().clear().apply()
    }


    fun formatDate(firebaseTimestamp: Long): String {
        val date = Date(firebaseTimestamp)
        val inputFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        return inputFormat.format(date)
    }

    fun formatTimeHHMM(firebaseTimestamp: Long): String {
        val date = Date(firebaseTimestamp)
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return dateFormat.format(date).toString().toUpperCase()
    }

    fun getItemsMapType(list: ArrayList<OrderItemDetails>): HashMap<String, Int> {
        val hashMap = HashMap<String, Int>()

        for (i in 0 until list.size) {
            val itemName = list[i].name
            val itemQuantity = list[i].quantity
            val mapped = hashMapOf(Pair(first = itemName, second = itemQuantity.toInt()))
            hashMap.putAll(mapped)
        }
        return hashMap

    }

    @JvmStatic
    @BindingAdapter("onEditorAction")
    fun EditText.bindOnEditorAction(onEditorAction: () -> Boolean) {
        setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onEditorAction.invoke()
                true
            } else {
                false
            }
        }
    }

    @JvmStatic
    @BindingAdapter("onTextChanged")
    fun EditText.bindOnTextChanged(onTextChanged: (text: String) -> Unit) {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                onTextChanged.invoke(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    @SuppressLint("MissingPermission")
    fun isDeviceConnected(deviceName: String): Boolean {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val bondedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
        if (bondedDevices != null && bondedDevices.isNotEmpty()) {
            for (device in bondedDevices) {
                if (device.name == deviceName) {
                    val deviceState: Int =
                        bluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP)
                    return deviceState == BluetoothProfile.STATE_CONNECTED
                }
            }
        }
        return false
    }

    fun getAsyncEscPosPrinter(
        orderModel: OrdersModel,
        printerConnection: DeviceConnection?,
        activity: Activity
    ): AsyncEscPosPrinter? {
        val format = SimpleDateFormat("'on' yyyy-MM-dd 'at' HH:mm:ss")
        val printer = AsyncEscPosPrinter(printerConnection, 203, 48f, 32)
        return printer.addTextToPrint(createPrintSlip(orderModel, printer, activity))
    }


    private fun orderType(payment: OrdersModel): String {
        if (payment.order?.details!!.orderType == null || payment.order?.details!!.orderType == "") {
            return "Dine In"
        } else {
            return payment.order?.details!!.orderType
        }
    }

    fun createPrintSlip(
        payment: OrdersModel,
        printer: AsyncEscPosPrinter,
        activity: Activity
    ): String? {
        var slip = ""
        var spaces = ""

        slip = "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(
            printer,
            activity.resources.getDrawableForDensity(R.drawable.new_zing, DisplayMetrics.DENSITY_HIGH)
        ) + "</img>\n";
        /*slip = "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, getDrawableForDensity(
            R.drawable.title_logo, DisplayMetrics.DENSITY_MEDIUM))+"</img>\n";*/

        slip += "[L]\n"
        slip += "[L]<b>Order type : " + getSpaces("Order type : ",orderType(payment))+orderType(payment)+"\n\n"
        /*slip += """
             ${"[R]<font size='normal'>        " + "[R]${orderType(payment)}"}</font>

             """.trimIndent()*/

        slip += "[L]<b>Order No. : " + getSpaces(" Order No.: ", payment.order?.details!!.orderId)+payment.order?.details!!.orderId+"\n\n"
        /*slip += """
             ${"[R]<font size='normal'>        " + "[R]${payment.orderNo}"}</font>

             """.trimIndent()*/
        slip += "[L]<b>" + "Order From : " + getSpaces("Order From : ", payment.customer?.details!!.name.split(" ")[0])+payment.customer?.details!!.name.split(" ")[0]+"\n\n"
        /*slip += """
             ${"[R]<font size='normal'>        " + "[R]${payment.userName.split(" ")[0]}"}</font>

             """.trimIndent()*/
        //slip += "[L]<font size='big'>" + Dataholder.printingPayment.orderType + "           #" + Dataholder.printingPayment.getPaymentOrderID().substring(Dataholder.printingPayment.getPaymentOrderID().length()-4) + "</font>\n";
        //slip += "[L]<font size='big'>Order from        " + Dataholder.printingPayment.getUserName().toUpperCase() + "</font>\n";
        //Add phone no here
        slip += "[C]<b>=============================================\n\n"
        for (i in 0 until payment.orderItem?.details!!.size) {
            spaces = getSpaces(payment.orderItem!!.details.get(i).name, "x2")
            slip += "[L]<font size='big-4'>" + payment.orderItem!!.details.get(i).name + spaces + "x" + payment.orderItem!!.details.get(
                i
            ).quantity + "\n\n"
            "</font>"


            /*slip += "[L]<font size='big-4'>${spaces}X" + payment.orderItems.get(i).itemQuantity +"\n\n"
            "</font>"*/


            // """.trimIndent()
        }
        slip += "[C]<b>=============================================\n\n"
        slip += """
             ${"[R]<font size='big-4'>                        Total Amount: " + payment.order!!.details!!.total}</font>

             """.trimIndent()
        return slip
    }


    /*fun createPrintSlip(
        payment: OrdersModel,
        printer: AsyncEscPosPrinter,
        context: Context
    ): String? {
        var slip = ""
        var spaces = ""

        slip = "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(
            printer,
            context.getResources()
                .getDrawableForDensity(R.drawable.new_zing, DisplayMetrics.DENSITY_HIGH)
        ) + "</img>\n";
        *//*slip = "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, getDrawableForDensity(
            R.drawable.title_logo, DisplayMetrics.DENSITY_MEDIUM))+"</img>\n";*//*

        slip += "[L]\n"
        slip += "[L]<b>Order type : "
        slip += """
             ${"[R]<font size='normal'>        " + "[R]${orderType(payment)}"}</font>
             
             """.trimIndent()

        slip += "[L]<b>Order No. : "
        slip += """
             ${"[R]<font size='normal'>        " + "[R]${payment.orderNo}"}</font>
             
             """.trimIndent()
        slip += "[L]<b>" + "Order From : "
        slip += """
             ${"[R]<font size='normal'>        " + "[R]${payment.userName.split(" ")[0]}"}</font>
             
             """.trimIndent()
        //slip += "[L]<font size='big'>" + Dataholder.printingPayment.orderType + "           #" + Dataholder.printingPayment.getPaymentOrderID().substring(Dataholder.printingPayment.getPaymentOrderID().length()-4) + "</font>\n";
        //slip += "[L]<font size='big'>Order from        " + Dataholder.printingPayment.getUserName().toUpperCase() + "</font>\n";
        //Add phone no here
        slip += "[C]<b>=============================================\n"
        for (i in 0 until payment.orderItems.size) {
            spaces = getSpaces(payment.orderItems.get(i).itemName)
            slip += "[L]<font size='big-4'>" + payment.orderItems.get(i).itemName + spaces + "X" + payment.orderItems.get(
                i
            ).itemQuantity + "\n\n"
            "</font>"


            *//*slip += "[L]<font size='big-4'>${spaces}X" + payment.orderItems.get(i).itemQuantity +"\n\n"
            "</font>"*//*


            // """.trimIndent()
        }
        slip += "[C]<b>=============================================\n"
        slip += """
             ${"[R]<font size='big-4'>                        Total Amount: " + payment.basePrice}</font>
             
             """.trimIndent()
        return slip
    }*/

    fun checkPermissions(activity: Activity) {
        val permission1 =
            ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permission2 =
            ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_SCAN)
        if (permission1 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                activity, arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                1
            )
        } else if (permission2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity, arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                1
            )
        }
    }

//    fun getSpaces(item: String): String {
//        Log.e(TAG, "Item Length ${item.length}")
//        var itemLength = item.length
//        var spaces = ""
//        // var count = if(itemLength>=16) 23 - (itemLength-16) else 17 + (16-itemLength)
//        var count = if (itemLength >= 16) 25 - (itemLength - 16) else 25 + (16 - itemLength)
//        for (i in 1..count)
//            spaces += " "
//        return spaces
//    }

    fun getSpaces(item: String, right: String): String {
        Log.e(TAG, "Item Length ${item.length}")
        var itemLength = item.length
        var spaces = ""
        // var count = if(itemLength>=16) 23 - (itemLength-16) else 17 + (16-itemLength)
        var count = if (itemLength >= 16) 25 - (itemLength - 16) else 25 + (16 - itemLength)+2-right.length
        for (i in 1..count)
            spaces += " "
        return spaces
    }


    fun printBluetooth(
        activity: Activity,
        context: Context,
        ordersModel: OrdersModel,
        id: String,
        firestore: FirebaseFirestore,
        bluetoothConnection: BluetoothConnection
    ) {
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
                arrayOf(Manifest.permission.BLUETOOTH_ADMIN),
                NewOrderFragment.PERMISSION_BLUETOOTH_ADMIN
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                NewOrderFragment.PERMISSION_BLUETOOTH_CONNECT
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.BLUETOOTH_SCAN),
                PERMISSION_BLUETOOTH_SCAN
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
                            Log.d(TAG, "id passed to printBluetooth is ${id}")
                            firestore.collection("prod_order")
                                .whereEqualTo("order.details.orderID", id).get()
                                .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
                                    if (task.isSuccessful) {
                                        for (documentSnapshot in task.result.documents) {
                                            // here you can get the id.
                                            Log.d(TAG, "Document got is ${documentSnapshot.data}")
                                            firestore.runTransaction { transaction ->
                                                transaction.update(
                                                    documentSnapshot.reference,
                                                    "zingDetails.status",
                                                    "2"
                                                )
                                            }.addOnSuccessListener {
                                                Log.d(TAG, "Transaction success! new")

                                            }
                                                .addOnFailureListener { e ->
                                                    Toast.makeText(
                                                        context,
                                                        "Error $e",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                            // you can apply your actions...
                                        }
                                    } else {
                                        Log.d(TAG, "Error in getting document ref")
                                    }
                                })
                            Toast.makeText(
                                context,
                                "Print is finished ! $id",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                         catch (e: Exception) {
                            Toast.makeText(context, "Error $e", Toast.LENGTH_LONG).show()
                        }
                    }

                }
            )
                .execute(getAsyncEscPosPrinter(ordersModel, bluetoothConnection, activity))
        }
    }


}

