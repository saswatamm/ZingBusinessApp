package com.zingit.restaurant.models

data class PaymentModel(
    val basePrice: String,
    val collectedTime: String,
    val couponDiscount: String,
    val id:String,
    val couponID:String,
    val orderItem: ArrayList<OrderItem>,
    val outletID:String,
    val paymentOrderID:String,
    val preparedTime:String,
    val reactionTime:String,
    val statusCode:Int,
    val taxesAndCharges:Float,
    val totalAmountPaid:Float,
    val userID: String,
    val userName: String,
    val zingTime: String
    )