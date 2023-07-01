package com.zingit.restaurant.viewModel

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat.stopForeground
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.zingit.restaurant.R
import com.zingit.restaurant.models.ApiResult
import com.zingit.restaurant.models.WhatsappRequestModel
import com.zingit.restaurant.models.order.OrdersModel
import com.zingit.restaurant.models.refund.RefundModel
import com.zingit.restaurant.models.refund.RequestRefund
import com.zingit.restaurant.network.Constants
import com.zingit.restaurant.repository.ZingRepository
import com.zingit.restaurant.service.CountdownService
import com.zingit.restaurant.utils.Utils
import com.zingit.restaurant.utils.Utils.hideKeyboard
import dagger.Provides
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@HiltViewModel
class OrderDetailsViewModel @Inject constructor(private var repository: ZingRepository) :
    ViewModel() {
    private  val TAG = "OrderDetailsViewModel"

    private val loading: MutableLiveData<Boolean> = MutableLiveData()
    val loadingLivedata: LiveData<Boolean>
        get() = loading
    lateinit var firestore: FirebaseFirestore
    val remainingTimeFinal: MutableLiveData<String> = MutableLiveData()

    private val _error: MutableLiveData<String> = MutableLiveData()
    val error: LiveData<String>
        get() = _error

    private val refund: MutableLiveData<RefundModel> = MutableLiveData()
    val refundLiveData: LiveData<RefundModel>
        get() = refund
    private val data: MutableLiveData<String> = MutableLiveData()
    val dataLivedata: LiveData<String>
        get() = data
    val checkFinish: MutableLiveData<Boolean> = MutableLiveData()

    private val _successMethod: MutableLiveData<Boolean> = MutableLiveData()
    val successMethod: LiveData<Boolean>
        get() = _successMethod

    private lateinit var timer: CountDownTimer

    init {
        firestore = FirebaseFirestore.getInstance()
        startCountdownTimer()
    }



    fun getRefund(amount:Double,merchantId:String,originalTransactionId:String,refundTransactionId:String){
        Constants.PROD_URL = "https://us-central1-zing-user.cloudfunctions.net/"
        Log.e(TAG, "getRefund: ${Constants.PROD_URL}", )
        viewModelScope.launch {
            loading.value = true
            val result = repository.getRefund(RequestRefund(amount,merchantId,originalTransactionId,refundTransactionId))
            when (result.status) {
                ApiResult.Status.SUCCESS -> {
                    loading.value = false
                    refund.value = result.data!!
                }
                ApiResult.Status.ERROR -> {
                    loading.value = false
                    _error.value = result.message!!
                }
                else -> {
                    loading.value = false
                    _error.value = result.message!!
                }
            }
        }



    }


    fun whatsappToUser(
        userName: String,
        orderNumber: String,
        hashMap: HashMap<String, Int>,
        mobileNumber: String,
        type: String,
        isAccept : Boolean,
        id: String
    ) {
        Constants.PROD_URL= "https://cy4eialxu7.execute-api.ap-south-1.amazonaws.com"
        viewModelScope.launch {
            loading.value = true
            val result = repository.callWhatsapp(
                WhatsappRequestModel(
                    "https://msrit.zingnow.in/",
                    userName,
                    orderNumber,
                    hashMap,
                    mobileNumber,
                    "RoadSide Adda",
                    type,
                    "15"
                )
            )

            when (result.status) {
                ApiResult.Status.SUCCESS -> {
                    loading.value = false
                    data.value = result.data!!.message
                    if (isAccept){
                        _successMethod.value = true
                        firestore.collection("payment").document(id).update("statusCode",3)
                    }else{
                        firestore.collection("payment").document(id).update("statusCode",-1)
                    }
                }
                ApiResult.Status.ERROR -> {
                    _successMethod.value = false
                    loading.value = false
                    _error.value = result.message!!
                }
                else -> {
                    _successMethod.value = false
                    loading.value = false
                    _error.value = "Something went wrong"
                }

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



}