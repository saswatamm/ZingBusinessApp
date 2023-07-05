package com.zingit.restaurant.models.orderGenerator


import com.google.gson.annotations.SerializedName

data class OrderGeneratorResponse(
    @SerializedName("msg")
    val msg: String,
    @SerializedName("status")
    val status: String
)