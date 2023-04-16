package com.zingit.restaurant.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.zingit.restaurant.models.*
import com.zingit.restaurant.repository.ZingRepository
import com.zingit.restaurant.utils.Utils.checkContactNumber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SignUpLoginViewModel @Inject constructor(private var repository: ZingRepository):androidx.lifecycle.ViewModel(){

    private  val TAG = "SignUpLoginViewModel"
    val number: MutableLiveData<String> = MutableLiveData()
    private val getOtpMutableLiveData = MutableLiveData<GetOTPResponse>()
    val getOtpLiveData: LiveData<GetOTPResponse>
        get() = getOtpMutableLiveData

     private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val verifyOtpMutableLiveData = MutableLiveData<VerifyOtpResponse>()
    val verifyOtpLiveData: LiveData<VerifyOtpResponse>
        get() = verifyOtpMutableLiveData

    private val loading:MutableLiveData<Boolean> = MutableLiveData()
    val loadingLivedata:LiveData<Boolean>
        get()=loading

    private val _error:MutableLiveData<String> = MutableLiveData()
    val error:LiveData<String>
        get()=_error
    private val data:MutableLiveData<String> = MutableLiveData()
    val dataLivedata:LiveData<String>
        get()=data



    fun isTenDigitNumber(): Boolean {
        return number.value?.length == 10
    }







    fun getOtp(){

        if (number.value.isNullOrEmpty()) {
            _error.value = "Please enter number"

        }else if(!checkContactNumber(number.value!!)){
            _error.value = "Please enter valid number"
        }else{
            viewModelScope.launch{
                loading.value=true
                val result = repository.getOtp(GetotpDTO(number.value!!))
                when (result.status) {
                    ApiResult.Status.SUCCESS -> {
                        loading.value=false
                        getOtpMutableLiveData.value=result.data!!
                    }
                    ApiResult.Status.ERROR -> {
                        loading.value=false
                        _error.value = result.message!!
                    }
                    else -> {}
                }
            }
        }

    }
    fun verifyOtp(verifyOtpDTO: VerifyOtpDTO) {
        viewModelScope.launch{
            loading.value=true
            val result = repository.verifyOtp(verifyOtpDTO)
            when (result.status) {
                ApiResult.Status.SUCCESS -> {
                    loading.value=false
                    verifyOtpMutableLiveData.value=result.data!!
                }
                ApiResult.Status.ERROR -> {
                    loading.value=false
                    _error.value = result.message!!
                }
                else -> {}
            }
        }
    }

    fun getDocumentIdData(context: Context, id:String) {
        viewModelScope.launch {

        }
    }

    fun fetchUsersData(){
        viewModelScope.launch {

        }
    }

    fun whatsappToUser(userName:String,orderNumber:String,hashMap: HashMap<String,Int>,mobileNumber:String,restaurant:String,type:String){
        viewModelScope.launch {
            loading.value=true
            val result = repository.callWhatsapp(WhatsappRequestModel("https://msrit.zingnow.in/",userName,orderNumber,hashMap,mobileNumber,restaurant,type,"15"))
            when (result.status) {
                ApiResult.Status.SUCCESS -> {
                    loading.value=false
                    data.value=result.data!!.message
                }
                ApiResult.Status.ERROR -> {
                    loading.value=false
                    _error.value = result.message!!
                }
                else -> {}
            }
        }
    }

}