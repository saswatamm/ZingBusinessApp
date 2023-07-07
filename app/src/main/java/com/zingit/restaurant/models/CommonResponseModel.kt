package com.zingit.restaurant.models


import com.google.gson.annotations.SerializedName

data class CommonResponseModel(
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)