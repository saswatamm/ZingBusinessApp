package com.zingit.restaurant.models.whatsapp

import com.google.gson.annotations.SerializedName

data class WhatsappPreparedModel(
    @SerializedName("destination")
    val destination: String,
    @SerializedName("orderNumber")
    val orderNumber: String,
    @SerializedName("restaurantName")
    val restaurantName: String,
    @SerializedName("userName")
    val userName: String,

)