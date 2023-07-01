package com.zingit.restaurant.models.order

import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.SerializedName

data class OrderZingDetails(
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: String?="",
    @get:PropertyName("acceptedTimestamp")
    @set:PropertyName("acceptedTimestamp")
    var acceptedTimestamp: String?="",
    @get:PropertyName("customerLat")
    @set:PropertyName("customerLat")
    var customerLat: Int?=0,
    @get:PropertyName("customerLong")
    @set:PropertyName("customerLong")
    var customerLong: Int?=0,
    @get:PropertyName("deniedTimestamp")
    @set:PropertyName("deniedTimestamp")
    var deniedTimestamp: String?="",
    @SerializedName("is_petpooja_integrated")
    @field:JvmField
    val isPetpoojaIntegrated: Boolean? = false,
    @get:PropertyName("modeSelected")
    @set:PropertyName("modeSelected")
    var modeSelected: String?="",
    @get:PropertyName("orderUnderConflict")
    @set:PropertyName("orderUnderConflict")
    var orderUnderConflict: String?="",
    @get:PropertyName("paymentOrderID")
    @set:PropertyName("paymentOrderID")
    var paymentOrderId: String?="",
    @get:PropertyName("preparedTimestamp")
    @set:PropertyName("preparedTimestamp")
    var preparedTimestamp: String?="",
    @get:PropertyName("qrID")
    @set:PropertyName("qrID")
    var qrID: String?="",
    @SerializedName("qrScanned")
    @field:JvmField
    val qrScanned: Boolean? = false,
    @get:PropertyName("refundProcessTime")
    @set:PropertyName("refundProcessTime")
    var refundProcessTime: String?="",
    @get:PropertyName("refundProcessed")
    @set:PropertyName("refundProcessed")
    var refundProcessed: String?="",
    @get:PropertyName("refundReason")
    @set:PropertyName("refundReason")
    var refundReason: String?="",
    @get:PropertyName("status")
    @set:PropertyName("status")
    var status: String?="",
//    @get:PropertyName("tableNumber")
//    @set:PropertyName("tableNumber")
//    var tableNumber: String?="",
    @get:PropertyName("ticketStatus")
    @set:PropertyName("ticketStatus")
    var ticketStatus: String?="",
)
