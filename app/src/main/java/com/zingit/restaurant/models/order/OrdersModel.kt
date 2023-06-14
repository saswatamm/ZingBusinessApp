package com.zingit.restaurant.models.order

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList


data class OrdersModel(
    @get:PropertyName("customer")
    @set:PropertyName("customer")
    var customer: OrderCustomerModel,
    @get:PropertyName("order")
    @set:PropertyName("order")
    var order: OrderDetailsModel,
    @get:PropertyName("orderItem")
    @set:PropertyName("orderItem")
    var orderItem:OrderItem ,
    @get:PropertyName("restaurant")
    @set:PropertyName("restaurant")
    var restaurant: OrderRestaurantModel,
    @get:PropertyName("tax")
    @set:PropertyName("tax")
    var tax:OrderTax,
)