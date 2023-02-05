package com.zingit.restaurant.models


import com.google.gson.annotations.SerializedName
data class VerifyOtpDTO(
    @SerializedName("mobileNo")
    val mobileNo:String,
    @SerializedName("otp")
    val otp:String)
