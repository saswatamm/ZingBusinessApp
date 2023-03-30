package com.zingit.restaurant.models

data class OrderItem(
    val itemID: String="",
    val itemImage: String="",
    val itemName: String="",
    val itemQuantity: Long=0,
    val itemTotal: Long=0
)
