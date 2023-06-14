package com.zingit.restaurant.models.order

import com.google.firebase.firestore.PropertyName

data class OrderRestaurantModel(
    @get:PropertyName("address")
    @set:PropertyName("address")
    var address: String = "",
    @get:PropertyName("contact_information")
    @set:PropertyName("contact_information")
    var contactInfo: String = "",
    @get:PropertyName("res_name")
    @set:PropertyName("res_name")
    var restaurantName: String = "",
    @get:PropertyName("restID")
    @set:PropertyName("restID")
    var restaurantId: String = "",
)
