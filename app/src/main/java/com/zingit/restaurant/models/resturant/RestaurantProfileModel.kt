package com.zingit.restaurant.models.resturant

import com.google.errorprone.annotations.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class RestaurantProfileModel(
    @SerializedName("campusID")
    val campusID: String ="",
    @SerializedName("category")
    val category: Int =0,
    @SerializedName("deliveryCharges")
    val deliveryCharges: Int = 0,
    @SerializedName("description")
    val description: String ="",
    @SerializedName("id")
    val id: String ="",
    @SerializedName("isDelivery")
    @field:JvmField
    val isDelivery:  Boolean =false,
    @SerializedName("isPrinterAvailable")
    @field:JvmField
    val isPrinterAvailable: Boolean = false,
    @SerializedName("latitude")
    val latitude: String ="",
    @SerializedName("longitude")
    val longitude: String ="",
    @SerializedName("name")
    val name: String ="",
    @SerializedName("numOrders")
    val numOrders: Int=0,
    @SerializedName("openStatus")
    val openStatus: String="",
    @SerializedName("outletImage")
    val outletImage: String = "",
    @SerializedName("takeAwayCharges")
    val takeAwayCharges: Int = 0,
)