package com.zingit.restaurant.repository

import android.content.Context
import com.zingit.restaurant.models.*
import com.zingit.restaurant.network.ApiEndPoints
import com.zingit.restaurant.network.ApiUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ZingRepository @Inject constructor(val apiInterFace: ApiEndPoints,private val apiUtils: ApiUtils, @ApplicationContext private val context: Context) {

    suspend fun getOtp(getotpDTO:GetotpDTO):ApiResult<GetOTPResponse>{
        return apiUtils.getResponse(context,"Oops Something went wrong"){
            apiInterFace.getOtp(getotpDTO)
        }
    }

    suspend fun verifyOtp(verifyOtpDTO: VerifyOtpDTO): ApiResult<VerifyOtpResponse> {
        return apiUtils.getResponse(context, "Oops Something went wrong") {
            apiInterFace.verifyOtp(
                verifyOtpDTO
            )
        }
    }

    suspend fun callWhatsapp(whatsappRequestModel:WhatsappRequestModel):ApiResult<WhatsAppResponse>{
        return apiUtils.getResponse(context,"Oops Something went wrong") {
            apiInterFace.callWhatsapp(whatsappRequestModel)
        }
    }

}