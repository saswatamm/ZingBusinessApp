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
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.res.ResourcesCompat.getDrawableForDensity
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import com.dantsu.escposprinter.connection.DeviceConnection
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firestore.v1.StructuredQuery.Order
import com.zingit.restaurant.R
import com.zingit.restaurant.models.PaymentModel
import com.zingit.restaurant.models.order.OrderItem
import com.zingit.restaurant.models.order.OrdersModel
import com.zingit.restaurant.utils.printer.AsyncEscPosPrinter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import kotlin.time.Duration.Companion.seconds


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
    fun convertToIsoString(date:Date): String {
        // Convert the Timestamp object to a Date object

        // Create a SimpleDateFormat to format the Date object
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
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
    ) {
        val sharedPreference = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putString("account_id", account_id)
        editor.putString("email", email)
        editor.putString("outlet_id", outlet_id)
        editor.commit()
    }


    fun getUserInfo(context: Context): String? {
        val sharedPreference = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        return sharedPreference.getString("account_id", null)
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

    fun getItemsMapType(list: ArrayList<OrderItem>): HashMap<String, Int> {
        val hashMap = HashMap<String, Int>()


        for (i in 0 until list.size) {
            val itemName = list[i].itemName
            val itemQuantity = list[i].itemQuantity
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
                    val deviceState: Int = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP)
                    return deviceState == BluetoothProfile.STATE_CONNECTED
                }
            }
        }
        return false
    }

    fun getAsyncEscPosPrinter(orderModel: OrdersModel,printerConnection: DeviceConnection?, context: Context): AsyncEscPosPrinter? {
        val format = SimpleDateFormat("'on' yyyy-MM-dd 'at' HH:mm:ss")
        val printer = AsyncEscPosPrinter(printerConnection, 203, 48f, 32)
        return printer.addTextToPrint(createPrintSlip(orderModel,printer,context))
    }
    fun createPrintSlip(payment: OrdersModel, printer: AsyncEscPosPrinter,context: Context): String? {
        var slip = ""
        var spaces = ""

        slip = "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, context.getResources().getDrawableForDensity(R.drawable.title_logo, DisplayMetrics.DENSITY_MEDIUM))+"</img>\n";
        /*slip = "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, getDrawableForDensity(
            R.drawable.title_logo, DisplayMetrics.DENSITY_MEDIUM))+"</img>\n";*/

        slip += "[L]\n"
        slip += "[L]<b>Order type : "
        slip += """
             ${"[R]<font size='big'>        " + payment.orderType}</font>
             
             """.trimIndent()

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
            spaces = getSpaces(payment.orderItems.get(i).itemName)
            slip += "[L]<font size='big-4'>" + payment.orderItems.get(i).itemName + spaces + "X"+  payment.orderItems.get(i).itemQuantity  +"\n\n"
            "</font>"


            /*slip += "[L]<font size='big-4'>${spaces}X" + payment.orderItems.get(i).itemQuantity +"\n\n"
            "</font>"*/
            
            
           // """.trimIndent()
        }
        slip += "[C]<b>=============================================\n"
        slip += """
             ${"[R]<font size='big-4'>                        Total Amount: " + payment.basePrice}</font>
             
             """.trimIndent()
        return slip
    }

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

    fun getSpaces(item: String) :String
    {
        Log.e(TAG,"Item Length ${item.length}")
        var itemLength = item.length
        var spaces = ""
       // var count = if(itemLength>=16) 23 - (itemLength-16) else 17 + (16-itemLength)
        var count = if(itemLength>=16) 25 - (itemLength-16) else 25 + (16-itemLength)
            for(i in 1..count)
                spaces += " "
        return spaces
    }


}

