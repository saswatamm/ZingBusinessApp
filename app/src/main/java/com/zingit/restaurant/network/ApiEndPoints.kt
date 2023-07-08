package com.zingit.restaurant.network

import com.zingit.restaurant.models.*
import com.zingit.restaurant.models.orderGenerator.OrderGeneratorResponse
import com.zingit.restaurant.models.orderGenerator.OrdergeneratorRequest
import com.zingit.restaurant.models.refund.PhonePeReq
import com.zingit.restaurant.models.refund.PhoneRefundResponseModel
import com.zingit.restaurant.models.whatsapp.WhatsappAcceptModel
import com.zingit.restaurant.models.whatsapp.WhatsappDeniedModel
import com.zingit.restaurant.models.whatsapp.WhatsappPreparedModel
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

    @POST("/aisensy/accepted")
    suspend fun whatsappAccepted(@Body whatsappAcceptedModel: WhatsappAcceptModel): Response<CommonResponseModel>

    @POST("/aisensy/prepared")
    suspend fun whatsappPrepared(@Body whatsappPreparedModel: WhatsappPreparedModel): Response<CommonResponseModel>
    @POST("/aisensy/denied")
    suspend fun whatsappDenied(@Body whatsappDeniedModel: WhatsappDeniedModel): Response<CommonResponseModel>


    @POST("/phoneperefund/refundPhonePe")
    suspend fun refundPhonePe(
     @Body phonePeReq: PhonePeReq
    ): Response<CommonResponseModel>


    @POST
    suspend fun clearOrderGenerator(
        @Url url: String,
        @Body orderGenerator: OrdergeneratorRequest
    ): Response<OrderGeneratorResponse>

}