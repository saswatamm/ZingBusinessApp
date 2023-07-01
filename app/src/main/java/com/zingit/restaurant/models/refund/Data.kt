package com.zingit.restaurant.models.refund


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("merchantId")
    val merchantId: String,
    @SerializedName("merchantTransactionId")
    val merchantTransactionId: String,
    @SerializedName("responseCode")
    val responseCode: String,
    @SerializedName("state")
    val state: String,
    @SerializedName("transactionId")
    val transactionId: String
)