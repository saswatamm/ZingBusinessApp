package com.zingit.restaurant.models


import com.google.gson.annotations.SerializedName

data class CommonResponseModel(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String

)