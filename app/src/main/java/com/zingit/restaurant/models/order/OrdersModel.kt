package com.zingit.restaurant.models.order

import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList

data class OrdersModel(
    val basePrice: Double=0.0,
    val collectedTime: String="",
    val couponDiscount: Double=0.0,
    val id:String="",
    val couponID:String="",
    val orderItems: ArrayList<OrderItem> = arrayListOf(),
    val orderType:String="",
    val outletID:String="",
    val orderNo:String="",
    val paymentOrderID:String="",
    val preparedTime:String="",
    val reactionTime:String="",
    val statusCode:Int=0,
    val taxesAndCharges:Float=0f,
    val totalAmountPaid:Float=0f,
    val userID: String="",
    val userName: String="",
    val zingTime: String="",
    var placedTime: com.google.firebase.Timestamp= com.google.firebase.Timestamp.now(),
)