package com.zingit.restaurant.network

import com.zingit.restaurant.models.*
import com.zingit.restaurant.models.orderGenerator.OrderGeneratorResponse
import com.zingit.restaurant.models.orderGenerator.OrdergeneratorRequest
import com.zingit.restaurant.models.refund.PhoneRefundResponseModel
import dagger.Provides
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url


interface ApiEndPoints {

    @POST("sendOtp")
    suspend fun getOtp(@Body getotpDTO: GetotpDTO): Response<GetOTPResponse>

    @POST("verifyOtp")
    suspend fun verifyOtp(@Body verifyOtpDTO: VerifyOtpDTO): Response<VerifyOtpResponse>

    @POST("/prod/api/v1/whatsapp")
    suspend fun callWhatsapp(@Body whatsappRequestModel: WhatsappRequestModel): Response<WhatsAppResponse>

    @GET("phoneperefund/refundPhonePe")
    suspend fun refundPhonePe(
        @Query("merchantUserId") merchantUserId: String,
        @Query("originalTransactionId") originalTransactionId: String,
        @Query("refundTransactionId") refundTransactionId: String,
        @Query("amount") amount: String,
        @Query("callbackUrl") callbackUrl: String,
    ): Response<PhoneRefundResponseModel>


    @POST("/clearOrderNumber")
    suspend fun clearOrderGenerator(
        @Url url: String,
        @Body orderGenerator: OrdergeneratorRequest
    ): Response<OrderGeneratorResponse>

}