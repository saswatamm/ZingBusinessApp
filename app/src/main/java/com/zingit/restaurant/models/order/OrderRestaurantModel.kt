package com.zingit.restaurant.models.order

import com.google.firebase.firestore.PropertyName

data class OrderRestaurantModel(
    @get:PropertyName("details")
    @set:PropertyName("details")
    var details:OrderRestaurantModelDetails=OrderRestaurantModelDetails(),
)
data class OrderRestaurantModelDetails(
    @get:PropertyName("address")
    @set:PropertyName("address")
    var address: String = "",
    @get:PropertyName("restaurant_name")
    @set:PropertyName("restaurant_name")
    var restaurant_name: String = "",
    @get:PropertyName("restaurant_id")
    @set:PropertyName("restaurant_id")
    var restaurant_id: String = "",
)
