package com.zingit.restaurant.network

import com.zingit.restaurant.models.GetOTPResponse
import com.zingit.restaurant.models.GetotpDTO
import com.zingit.restaurant.models.VerifyOtpDTO
import com.zingit.restaurant.models.VerifyOtpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiEndPoints {

    @POST("sendOtp")
    suspend fun getOtp(@Body getotpDTO: GetotpDTO): Response<GetOTPResponse>

    @POST("verifyOtp")
    suspend fun verifyOtp(@Body verifyOtpDTO: VerifyOtpDTO): Response<VerifyOtpResponse>

}