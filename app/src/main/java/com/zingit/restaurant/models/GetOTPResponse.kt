package com.zingit.restaurant.models


import com.google.gson.annotations.SerializedName

data class GetOTPResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Boolean
)