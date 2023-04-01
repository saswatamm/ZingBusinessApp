package com.zingit.restaurant.models

import com.google.gson.annotations.SerializedName

data class WhatsappRequestModel(
    @SerializedName("campus_url")
    val campus_url: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("order_number")
    val order_number: String,
    @SerializedName("ordered_items")
    val ordered_items: HashMap<String,Int>,
    @SerializedName("phone_number")
    val phone_number: String,
    @SerializedName("restaurant_name")
    val restaurant_name: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("zing_time")
    val zing_time: String

)