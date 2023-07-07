package com.zingit.restaurant.repository

import android.content.Context
import com.zingit.restaurant.models.*
import com.zingit.restaurant.models.orderGenerator.OrderGeneratorResponse
import com.zingit.restaurant.models.orderGenerator.OrdergeneratorRequest
import com.zingit.restaurant.models.refund.PhonePeReq
import com.zingit.restaurant.models.refund.PhoneRefundResponseModel
import com.zingit.restaurant.models.whatsapp.WhatsappAcceptModel
import com.zingit.restaurant.models.whatsapp.WhatsappDeniedModel
import com.zingit.restaurant.models.whatsapp.WhatsappPreparedModel
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

    suspend fun whatsappAccepted(whatsappAcceptedModel: WhatsappAcceptModel):ApiResult<CommonResponseModel>{
        return apiUtils.getResponse(context,"Oops Something went wrong") {
            apiInterFace.whatsappAccepted(whatsappAcceptedModel)
        }
    }

    suspend fun whatsappPrepared(whatsappPreparedModel: WhatsappPreparedModel):ApiResult<CommonResponseModel>{
        return apiUtils.getResponse(context,"Oops Something went wrong") {
            apiInterFace.whatsappPrepared(whatsappPreparedModel)
        }
    }

    suspend fun whatsappDenied(whatsappDeniedModel: WhatsappDeniedModel):ApiResult<CommonResponseModel>{
        return apiUtils.getResponse(context,"Oops Something went wrong") {
            apiInterFace.whatsappDenied(whatsappDeniedModel)
        }
    }

    suspend fun refundApi(
       phonePeReq: PhonePeReq
    ):ApiResult<CommonResponseModel>{
        return apiUtils.getResponse(context,"Oops Something went wrong") {
            apiInterFace.refundPhonePe(
                phonePeReq
            )
        }
    }

    suspend fun clearOrderGenerator(url:String,orderGenerator: OrdergeneratorRequest):ApiResult<OrderGeneratorResponse>{
        return apiUtils.getResponse(context,"Oops Something went wrong") {
            apiInterFace.clearOrderGenerator(url,orderGenerator)
        }
    }

}