package com.zingit.restaurant.network

import com.zingit.restaurant.models.*
import dagger.Provides
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST



interface ApiEndPoints {

    @POST("sendOtp")
    suspend fun getOtp(@Body getotpDTO: GetotpDTO): Response<GetOTPResponse>

    @POST("verifyOtp")
    suspend fun verifyOtp(@Body verifyOtpDTO: VerifyOtpDTO): Response<VerifyOtpResponse>

    @POST("/prod/api/v1/whatsapp")
    suspend fun callWhatsapp(@Body whatsappRequestModel: WhatsappRequestModel): Response<WhatsAppResponse>

}