package com.zingit.restaurant.models.order

import com.google.firebase.firestore.PropertyName

data class OrderCustomerModel(
    @get:PropertyName("address")
    @set:PropertyName("address")
    var address: String = "",
    @get:PropertyName("email")
    @set:PropertyName("email")
    var email: String = "",
    @get:PropertyName("latitude")
    @set:PropertyName("latitude")
    var latitude: String = "",
    @get:PropertyName("longitude")
    @set:PropertyName("longitude")
    var longitude: String = "",
    @get:PropertyName("name")
    @set:PropertyName("name")
    var name: String = "",
    @get:PropertyName("phone")
    @set:PropertyName("phone")
    var phone: String = "",
)
