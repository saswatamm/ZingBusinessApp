package com.zingit.restaurant.models.order

import com.google.firebase.firestore.PropertyName
import java.sql.Timestamp
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
)