package com.zingit.restaurant.models.order

import com.google.firebase.firestore.PropertyName
import java.util.*
import kotlin.collections.ArrayList


data class OrdersModel(

    @get:PropertyName("customer")
    @set:PropertyName("customer")
    var customer: OrderCustomerModel?=OrderCustomerModel(),
    @get:PropertyName("order")
    @set:PropertyName("order")
    var order: OrderDetailsModel?=OrderDetailsModel(),
    @get:PropertyName("orderItem")
    @set:PropertyName("orderItem")
    var orderItem:OrderItem? =OrderItem(),
    @get:PropertyName("restaurant")
    @set:PropertyName("restaurant")
    var restaurant: OrderRestaurantModel?=OrderRestaurantModel(),
    @get:PropertyName("tax")
    @set:PropertyName("tax")
    var tax:OrderTax?=OrderTax(),
//    @get:PropertyName("status")
//    @set:PropertyName("status")
//    var status: String?="",
    @get:PropertyName("zingDetails")
    @set:PropertyName("zingDetails")
    var zingDetails: OrderZingDetails?=OrderZingDetails(),
//    @get:PropertyName("cancel_reason")
//    @set:PropertyName("cancel_reason")
//    var cancelReason:String?="",
)