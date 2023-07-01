package com.zingit.restaurant.network

import com.google.gson.annotations.SerializedName
import com.zingit.restaurant.models.*
import com.zingit.restaurant.models.refund.RefundModel
import com.zingit.restaurant.models.refund.RequestRefund
import dagger.Provides
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface ApiEndPoints {

    @POST("sendOtp")
    suspend fun getOtp(@Body getotpDTO: GetotpDTO): Response<GetOTPResponse>

    @POST("verifyOtp")
    suspend fun verifyOtp(@Body verifyOtpDTO: VerifyOtpDTO): Response<VerifyOtpResponse>

    @POST("/prod/api/v1/whatsapp")
    suspend fun callWhatsapp(@Body whatsappRequestModel: WhatsappRequestModel): Response<WhatsAppResponse>


    @POST("/prod/api/v1/refund")
    suspend fun refundApi(
      @Body refund: RequestRefund
    ): Response<RefundModel>

}