package com.zingit.restaurant.models


import com.google.gson.annotations.SerializedName

data class GetotpDTO(
    @SerializedName("mobileNo")
    val mobileNo:String
)