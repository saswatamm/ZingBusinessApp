package com.zingit.restaurant.models.orderGenerator


import com.google.gson.annotations.SerializedName

data class OrdergeneratorRequest(
    @SerializedName("orderNumber")
    val orderNumber: String,
    @SerializedName("restId")
    val restId: String
)