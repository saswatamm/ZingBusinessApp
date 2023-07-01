package com.zingit.restaurant.repository

import android.content.Context
import com.zingit.restaurant.models.*
import com.zingit.restaurant.models.refund.RefundModel
import com.zingit.restaurant.models.refund.RequestRefund
import com.zingit.restaurant.network.ApiEndPoints
import com.zingit.restaurant.network.ApiUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ZingRepository @Inject constructor(val apiInterFace: ApiEndPoints,private val apiUtils: ApiUtils, @ApplicationContext private val context: Context) {



    suspend fun callWhatsapp(whatsappRequestModel:WhatsappRequestModel):ApiResult<WhatsAppResponse>{
        return apiUtils.getResponse(context,"Oops Something went wrong") {
            apiInterFace.callWhatsapp(whatsappRequestModel)
        }
    }
    suspend fun getRefund(requestRefund: RequestRefund):ApiResult<RefundModel>{
        return apiUtils.getResponse(context, "Oops Something went wrong") {
            apiInterFace.refundApi(requestRefund)
        }
    }

}