package com.zingit.restaurant.models.refund


import com.google.gson.annotations.SerializedName

data class RequestRefund(
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("merchantUserId")
    val merchantUserId: String,
    @SerializedName("originalTransactionId")
    val originalTransactionId: String,
    @SerializedName("refundTransactionId")
    val refundTransactionId: String
)