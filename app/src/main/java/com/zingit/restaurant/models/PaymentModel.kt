package com.zingit.restaurant.models

data class PaymentModel(
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
    )

)

