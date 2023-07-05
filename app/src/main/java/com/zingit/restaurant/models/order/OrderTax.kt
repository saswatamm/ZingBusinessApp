package com.zingit.restaurant.models.order

import com.google.firebase.firestore.PropertyName

data class OrderTax(
    @get:PropertyName("details")
    @set:PropertyName("details")
    var taxDetails: ArrayList<TaxDetails> = arrayListOf(),
)
data class TaxDetails(
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: String = "",
    @get:PropertyName("price")
    @set:PropertyName("price")
    var price: String = "",
    @get:PropertyName("restaurant_liable_amt")
    @set:PropertyName("restaurant_liable_amt")
    var restLiableAmt: String = "",
    @get:PropertyName("tax")
    @set:PropertyName("tax")
    var tax: String = "",
    @get:PropertyName("title")
    @set:PropertyName("title")
    var title: String = "",
    @get:PropertyName("type")
    @set:PropertyName("type")
    var type: String = "",
)
