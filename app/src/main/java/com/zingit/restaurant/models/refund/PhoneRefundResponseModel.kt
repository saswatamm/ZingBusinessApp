package com.zingit.restaurant.models.refund


import com.google.gson.annotations.SerializedName

data class PhoneRefundResponseModel(
    @SerializedName("code")
    val code: String,
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Boolean
)