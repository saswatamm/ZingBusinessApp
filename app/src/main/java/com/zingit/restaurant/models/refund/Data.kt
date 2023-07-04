package com.zingit.restaurant.models.refund


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("merchantId")
    val merchantId: Any,
    @SerializedName("merchantTransactionId")
    val merchantTransactionId: Any,
    @SerializedName("responseCode")
    val responseCode: String,
    @SerializedName("state")
    val state: Any,
    @SerializedName("transactionId")
    val transactionId: Any
)