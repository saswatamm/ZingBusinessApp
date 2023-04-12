package com.zingit.restaurant.models.order

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


data class OrderItem(
    @SerializedName("itemID")
    val itemID: String="",
    @SerializedName("itemImage")
    val itemImage: String="",
    @SerializedName("itemName")
    val itemName: String="",
    @SerializedName("itemPrice")
    val itemQuantity: Long=0,
    @SerializedName("itemTotal")
    val itemTotal: Long=0
)
