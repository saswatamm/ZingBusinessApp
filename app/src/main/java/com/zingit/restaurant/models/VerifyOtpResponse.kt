package com.zingit.restaurant.models

import com.google.gson.annotations.SerializedName

data class VerifyOtpResponse(
    @SerializedName("accessToken")
    val access_token: String,
    @SerializedName("expiry")
    val expiry: Int,
    @SerializedName("newAccount")
    val newAccount: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Boolean
)