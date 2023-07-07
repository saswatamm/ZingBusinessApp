package com.zingit.restaurant.models.whatsapp

import com.google.gson.annotations.SerializedName

data class WhatsappDeniedModel(
    @SerializedName("destination")
    val destination: String,
    @SerializedName("orderNumber")
    val orderNumber: String,
    @SerializedName("userName")
    val userName: String,

    )