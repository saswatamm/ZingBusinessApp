package com.zingit.restaurant.models.refund

import com.google.gson.annotations.SerializedName

data class PhonePeReq(
    @SerializedName("merchantUserId")
    val merchantUserId: String,
    @SerializedName("originalTransactionId")
    val originalTransactionId: String,
    @SerializedName("refundTransactionId")
    val refundTransactionId: String,
    @SerializedName("amount")
    val amount: String,
    @SerializedName("callbackUrl")
    val callbackUrl: String

)
