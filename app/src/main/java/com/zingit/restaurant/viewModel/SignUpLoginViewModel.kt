package com.zingit.restaurant.viewModel

import android.app.Application
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.zingit.restaurant.models.*
import com.zingit.restaurant.repository.ZingRepository
import com.zingit.restaurant.utils.Utils
import com.zingit.restaurant.utils.Utils.hideKeyboard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SignUpLoginViewModel @Inject constructor(
    private val application: Application,
    private var repository: ZingRepository
) :
    androidx.lifecycle.ViewModel() {

    private val TAG = "SignUpLoginViewModel"
    val email: MutableLiveData<String> = MutableLiveData()
    val password: MutableLiveData<String> = MutableLiveData()
    private val getOtpMutableLiveData = MutableLiveData<GetOTPResponse>()
    val getOtpLiveData: LiveData<GetOTPResponse>
        get() = getOtpMutableLiveData

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val verifyOtpMutableLiveData = MutableLiveData<VerifyOtpResponse>()
    val verifyOtpLiveData: LiveData<VerifyOtpResponse>
        get() = verifyOtpMutableLiveData

    private val loading: MutableLiveData<Boolean> = MutableLiveData()
    val loadingLivedata: LiveData<Boolean>
        get() = loading

    private val _error: MutableLiveData<String> = MutableLiveData()
    val error: LiveData<String>
        get() = _error
    private val signIn: MutableLiveData<Boolean> = MutableLiveData()
    val dataSignIn: LiveData<Boolean>
        get() = signIn
    private val data: MutableLiveData<String> = MutableLiveData()
    val dataLiveData: LiveData<String>
        get() = data

    val mAuth = FirebaseAuth.getInstance()


    fun isEmailValid(): Boolean {
        return !TextUtils.isEmpty(email.value) && android.util.Patterns.EMAIL_ADDRESS.matcher(email.value)
            .matches()
    }


    fun signInWithUserPass(view: View) {
        view.hideKeyboard()
        viewModelScope.launch {
            if (isEmailValid() && !password.value.isNullOrEmpty()) {
                loading.value = true
                mAuth.signInWithEmailAndPassword(email.value!!, password.value!!)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            loading.value = false
                            Log.e(TAG, "signInWithUserPass: $it")
                            getOutlet()

                        } else {
                            loading.value = false
                            signIn.value = false
                            _error.value = it.exception?.message.toString()
                        }
                    }
            } else {
                if (email.value.isNullOrEmpty())
                    _error.value = "Email cannot be empty"
                else if(!isEmailValid()){
                    _error.value = "Please enter valid email"
                }else{
                    _error.value = "Password cannot be empty"
                }

            }

        }




    }

    fun signOut() {
        Utils.deleteUserInfo(application)
        mAuth.signOut()


    }

    fun getOutlet(){
        loading.value = true
        firestore.collection("outletAuth").whereEqualTo("email",mAuth.currentUser?.email).addSnapshotListener { value, error ->
            if (error != null) {
                loading.value = false
                Log.e(TAG, "signInWithUserPass: $error")
                return@addSnapshotListener
            }
            if (value != null) {
                loading.value = false
                val outletAuthModel = value.documents[0].toObject(OutletAuthModel::class.java)
                if (outletAuthModel != null) {
                    Log.e(TAG, "getOutlet: ${outletAuthModel.outletId}")
                    Log.e(TAG, "mAuth: ${mAuth.currentUser!!.email}")
                    Log.e(TAG, "getOutlet: ${outletAuthModel.outletId}")
                    Utils.insertUserInfo(application, mAuth.currentUser?.uid!!,  mAuth.currentUser?.email!!, value.documents.get(0).data?.get("outletID").toString(),value.documents.get(0).data?.get("outletID").toString(),value.documents.get(0).data?.get("printerMac").toString())
                    Log.e(TAG, "OutletIdSet:"+Utils.getUserOutletId(application))
                    signIn.value = true
                }
            }
        }
    }


    fun verifyOtp(verifyOtpDTO: VerifyOtpDTO) {
        viewModelScope.launch {
            loading.value = true
            val result = repository.verifyOtp(verifyOtpDTO)
            when (result.status) {
                ApiResult.Status.SUCCESS -> {
                    loading.value = false
                    verifyOtpMutableLiveData.value = result.data!!
                }
                ApiResult.Status.ERROR -> {
                    loading.value = false
                    _error.value = result.message!!
                }
                else -> {}
            }
        }
    }


    fun whatsappToUser(
        userName: String,
        orderNumber: String,
        hashMap: HashMap<String, Int>,
        mobileNumber: String,
        restaurant: String,
        type: String
    ) {
        viewModelScope.launch {
            loading.value = true
            val result = repository.callWhatsapp(
                WhatsappRequestModel(
                    "https://msrit.zingnow.in/",
                    userName,
                    orderNumber,
                    hashMap,
                    mobileNumber,
                    restaurant,
                    type,
                    "15"
                )
            )
            when (result.status) {
                ApiResult.Status.SUCCESS -> {
                    loading.value = false
                    data.value = result.data!!.message
                }
                ApiResult.Status.ERROR -> {
                    loading.value = false
                    _error.value = result.message!!
                }
                else -> {}
            }
        }
    }

}