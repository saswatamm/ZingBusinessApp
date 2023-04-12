package com.zingit.restaurant.models.order

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList


data class OrdersModel(
    @SerializedName("basePrice")
    val basePrice: Double=0.0,
    @SerializedName("collectedTime")
    val collectedTime: String="",
    @SerializedName("couponDiscount")
    val couponDiscount: Double=0.0,
    @SerializedName("id")
    val id:String="",
    @SerializedName("campusID")
    val couponID:String="",
    @SerializedName("orderItems")
    val orderItems: ArrayList<OrderItem> = arrayListOf(),
    @SerializedName("orderType")
    val orderType:String="",
    @SerializedName("outletID")
    val outletID:String="",
    @SerializedName("orderNo")
    val orderNo:String="",
    @SerializedName("paymentOrderID")
    val paymentOrderID:String="",
    @SerializedName("preparedTime")
    val preparedTime:String="",
    @SerializedName("reactionTime")
    val reactionTime:String="",
    @SerializedName("statusCode")
    val statusCode:Int=0,
    @SerializedName("taxesAndCharges")
    val taxesAndCharges:Float=0f,
    @SerializedName("totalAmountPaid")
    val totalAmountPaid:Float=0f,
    @SerializedName("userID")
    val userID: String="",
    @SerializedName("userName")
    val userName: String="",
    @SerializedName("userNumber")
    val userNumber: String="",
    @SerializedName("zingTime")
    val zingTime: String="",
    @SerializedName("placedTime")
    var placedTime: com.google.firebase.Timestamp= com.google.firebase.Timestamp.now(),
)