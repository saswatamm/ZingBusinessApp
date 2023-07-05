package com.zingit.restaurant.models.earning

import com.google.firebase.firestore.PropertyName

data class EarningModel (
    @set:PropertyName("created_on")
    @get:PropertyName("created_on")
    var createdOn: String? = "",
    @set:PropertyName("id")
    @get:PropertyName("id")
    var id: String? = "",
    @set:PropertyName("linkOrderTotal")
    @get:PropertyName("linkOrderTotal")
    var linkOrderTotal: String? = "",
    @set:PropertyName("linkTotalOrderNos")
    @get:PropertyName("linkTotalOrderNos")
    var linkTotalOrderNos: String? = "",
    @set:PropertyName("qrTotalOrderNos")
    @get:PropertyName("qrTotalOrderNos")
    var qrTotalOrderNos: String? = "",
    @set:PropertyName("qrOrderTotal")
    @get:PropertyName("qrOrderTotal")
    var qrOrderTotal: String? = "",
    @set:PropertyName("restaurantID")
    @get:PropertyName("restaurantID")
    var restaurantID: String? = "",
    @set:PropertyName("restaurant_name")
    @get:PropertyName("restaurant_name")
    var restaurantName: String? = "",
)