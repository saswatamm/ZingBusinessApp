package com.zingit.restaurant.viewModel

import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.zingit.restaurant.models.ApiResult
import com.zingit.restaurant.models.CommonResponseModel
import com.zingit.restaurant.models.order.OrdersModel
import com.zingit.restaurant.models.orderGenerator.OrderGeneratorResponse
import com.zingit.restaurant.models.orderGenerator.OrdergeneratorRequest
import com.zingit.restaurant.models.refund.PhonePeReq
import com.zingit.restaurant.models.refund.PhoneRefundResponseModel
import com.zingit.restaurant.models.whatsapp.WhatsappAcceptModel
import com.zingit.restaurant.models.whatsapp.WhatsappDeniedModel
import com.zingit.restaurant.models.whatsapp.WhatsappPreparedModel
import com.zingit.restaurant.repository.ZingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.wait
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@HiltViewModel
class OrderDetailsViewModel @Inject constructor(private var repository: ZingRepository) :
    ViewModel() {
    private val TAG = "OrderDetailsViewModel"

    private val loading: MutableLiveData<Boolean> = MutableLiveData()
    val loadingLivedata: LiveData<Boolean>
        get() = loading
    var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    val remainingTimeFinal: MutableLiveData<String> = MutableLiveData()

    private val data: MutableLiveData<CommonResponseModel> = MutableLiveData()
    val dataLivedata: LiveData<CommonResponseModel>
        get() = data
    val checkFinish: MutableLiveData<Boolean> = MutableLiveData()


    private val _whatsappPrepared: MutableLiveData<CommonResponseModel> = MutableLiveData()
    val whatsappPrepared: LiveData<CommonResponseModel>
        get() = _whatsappPrepared


    private val _whatsappDeniedData: MutableLiveData<CommonResponseModel> = MutableLiveData()
    val whatsappDeniedData: LiveData<CommonResponseModel>
        get() = _whatsappDeniedData





    private val _orderGeneratorResponse: MutableLiveData<OrderGeneratorResponse> = MutableLiveData()
    val orderGeneratorResponse: LiveData<OrderGeneratorResponse>
        get() = _orderGeneratorResponse

    private lateinit var timer: CountDownTimer

    init {
        startCountdownTimer()
    }

    fun cancelOrder(id: String) {
        firestore.collection("prod_order")
            .whereEqualTo("order.details.orderID", id)
            .whereEqualTo("restaurant.details.restaurant_id", "hi")
            .whereIn("zingDetails.status", mutableListOf("0", "1", "2"))
            .get()
            .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
                if (task.isSuccessful) {
                    for (documentSnapshot in task.result.documents) {
                        // here you can get the id.
                        Log.d(TAG, "Document got is ${documentSnapshot.data}")
                        firestore.runTransaction { transaction ->
                            transaction.update(
                                documentSnapshot.reference,
                                "zingDetails.status",
                                "-1"
                            )
                        }.addOnSuccessListener {

                        }
                            .addOnFailureListener { e ->
                                Log.d(TAG, "" + e.toString())
                            }
                        // you can apply your actions...
                    }
                } else {
                    Log.d(TAG, "Error in getting document ref")
                }
            })
    }


    fun whatsappAccepted(
        destination: String,
        orderNumber: String,
        restaurantName: String,
        userName: String,
        zingTime: String,

        ) {
        viewModelScope.launch {
            loading.value = true
            val result = repository.whatsappAccepted(
                WhatsappAcceptModel(
                    destination,
                    orderNumber,
                    restaurantName,
                    userName,
                    zingTime
                )
            )
            when (result.status) {
                ApiResult.Status.SUCCESS -> {
                    loading.value = false
                    data.value = result.data!!


                }

                ApiResult.Status.ERROR -> {

                    loading.value = false
                }

                else -> {

                    loading.value = false
                }

            }
        }

    }


    fun whatsappDenied(
        mobileNumber: String,
        orderNumber: String,
        userName: String,
        isAccept: Boolean,
        restId: String,


        ) {
        viewModelScope.launch {
            loading.value = true
            val result = repository.whatsappDenied(
                WhatsappDeniedModel(
                    mobileNumber,
                    orderNumber,
                    userName
                )
            )
            when (result.status) {
                ApiResult.Status.SUCCESS -> {
                    loading.value = false
                    _whatsappDeniedData.value = result.data!!

                }

                ApiResult.Status.ERROR -> {
                    _whatsappDeniedData.value = result.data!!
                    loading.value = false
                }

                else -> {
                    loading.value = false
                }

            }
        }

    }


    fun whatsappMultiOperation( mobileNumber: String,
                                orderNumber: String,
                                restaurantName: String,
                                userName: String,
                                isAccept: Boolean,
                                restId: String){
        viewModelScope.launch {
            val callWhatsappPreparedApi = async {
                whatsappPrepared(mobileNumber, orderNumber, restaurantName, userName, isAccept, restId)

            }

            val clearOrderNoApi = async {
                orderGenerator(orderNumber, restId)
            }



            val firebaseOperation  = async {
                firestore.collection("prod_order")
                    .whereEqualTo("order.details.orderID", orderNumber)
                    .whereEqualTo("restaurant.details.restaurant_id", restId)
                    .whereIn("zingDetails.status", mutableListOf("0", "1", "2"))
                    .get()
                    .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
                        if (task.isSuccessful) {
                            for (documentSnapshot in task.result.documents) {
                                // here you can get the id.
                                Log.d(TAG, "Document got is ${documentSnapshot.data}")
                                firestore.runTransaction { transaction ->
                                    transaction.update(
                                        documentSnapshot.reference,
                                        "zingDetails.status",
                                        "5"
                                    )
                                }.addOnSuccessListener {


                                }
                                    .addOnFailureListener { e ->
                                        Log.d(TAG, "" + e.toString())
                                    }
                                // you can apply your actions...
                            }
                        } else {
                            Log.d(TAG, "Error in getting document ref")
                        }
                    })
            }
            firebaseOperation.await()
            callWhatsappPreparedApi.await()
            clearOrderNoApi.await()


        }

    }


    fun whatsappPrepared(
        mobileNumber: String,
        orderNumber: String,
        restaurantName: String,
        userName: String,
        isAccept: Boolean,
        restId: String
    ) {

        viewModelScope.launch {
            loading.value = true

            val result =
                repository.whatsappPrepared(
                    WhatsappPreparedModel(
                        mobileNumber,
                        orderNumber,
                        restaurantName,
                        userName
                    )
                )
            when (result.status) {
                ApiResult.Status.SUCCESS -> {
                    loading.value = false
                    _whatsappPrepared.value = result.data!!
                }

                ApiResult.Status.ERROR -> {
                    _whatsappPrepared.value = result.data!!
                    loading.value = false
                }


                else -> {}
            }
        }

    }


    private fun startCountdownTimer() {
        //150000
        timer = object : CountDownTimer(60000, 1000) {
            @SuppressLint("MissingPermission")
            override fun onTick(millisUntilFinished: Long) {
                // Update the notification's content text with the remaining time in seconds
                val remainingTime = millisUntilFinished / 1000
                checkFinish.value = false
                val text = "Reject Order ${
                    String.format(
                        "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                            TimeUnit.MILLISECONDS.toMinutes(
                                millisUntilFinished
                            )
                        )
                    )
                }"
                remainingTimeFinal.value = text
            }


            override fun onFinish() {
                // Remove the notification and stop the service
                remainingTimeFinal.value = "Reject Order"
                checkFinish.value = true


            }
        }

        timer.start()
    }

    fun whatsappMultiOperationDenied(
        orderModel: OrdersModel,
        phonePeReq: PhonePeReq
    ){

        viewModelScope.launch {
            val firebaseUpdate = async {
                firebaseRefund(phonePeReq,orderModel)
            }

            val refundApi = async {
                refundApi(phonePeReq)
            }

            val clearOrderNoApi = async {
                orderGenerator(orderModel.order?.details!!.orderId, orderModel.restaurant?.details?.restaurant_id!!)
            }

            val whatsAppDenied = async {
                whatsappDenied(
                    orderModel.customer?.details!!.phone,
                    orderModel.order?.details!!.orderId,
                   orderModel.customer?.details!!.name,

                    false,
                    orderModel.restaurant?.details?.restaurant_id!!
                )
            }

            firebaseUpdate.await()
            clearOrderNoApi.await()
            whatsAppDenied.await()
            refundApi.await()

        }

    }


    fun firebaseRefund(phonePeReq: PhonePeReq,orderModel: OrdersModel){
        viewModelScope.launch {
            firestore.collection("prod_order")
                .whereEqualTo("order.details.orderID", orderModel.order?.details!!.orderId)
                .whereEqualTo(
                    "restaurant.details.restaurant_id",
                    orderModel.restaurant?.details?.restaurant_id
                )
                .whereIn("zingDetails.status", mutableListOf("0", "1", "2"))
                .get()
                .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
                    if (task.isSuccessful) {
                        for (documentSnapshot in task.result.documents) {
                            Log.d(TAG, "Document got is ${documentSnapshot.data}")
                            firestore.runTransaction { transaction ->
                                val currentTimeMillis = System.currentTimeMillis()
                                val date = Date(currentTimeMillis)
                                val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                val formattedDate = format.format(date)
                                transaction.update(
                                    documentSnapshot.reference,
                                    "zingDetails.refundProcessTime",
                                    formattedDate,
                                    "zingDetails.refundProcessed",
                                    orderModel.order?.details?.total,
                                    "zingDetails.refundReason",
                                    phonePeReq.refundTransactionId,
                                    "zingDetails.status",
                                    "-1",
                                )
                            }.addOnSuccessListener {

                            }
                                .addOnFailureListener { e ->
                                    Log.d(TAG, "" + e.toString())
                                }
                            // you can apply your actions...
                        }
                    } else {
                        Log.d(TAG, "Error in getting document ref")
                    }
                })

        }

    }


    fun refundApi(
     phonePeReq: PhonePeReq

    ) {
        viewModelScope.launch {
            loading.value = true
            Log.d("Refunding", "initiated")
            val result = repository.refundApi(
                phonePeReq = phonePeReq
            )
            when (result.status) {
                ApiResult.Status.SUCCESS -> {
                    Log.e("PhonePe Result Success", result.data!!.toString())
                    loading.value = false
                    data.value = result.data!!
                }

                ApiResult.Status.ERROR -> {
                    Log.e("PhonePe Result Error", result.message + ".")

                    loading.value = false

                }

                else -> {
                    Log.e("PhonePe Result Else", result.message + ".")

                    loading.value = false
                }

            }
        }
    }


    fun orderGenerator(
        orderNumber: String,
        restId: String,
    ) {
        val url = "https://ordergenerator-dev.zingnow.in/clearOrderNumber"
        viewModelScope.launch {
            loading.value = true
            val result =
                repository.clearOrderGenerator(
                    url,
                    OrdergeneratorRequest(
                        orderNumber,
                        restId
                    )
                )

            when (result.status) {
                ApiResult.Status.SUCCESS -> {
                    loading.value = false
                    _orderGeneratorResponse.value = result.data!!

                }

                ApiResult.Status.ERROR -> {

                    loading.value = false
                }

                else -> {
                    loading.value = false
                }

            }
        }
    }

}