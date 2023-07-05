package com.zingit.restaurant.models.item

import com.google.firebase.firestore.PropertyName

data class AddonItemsModel(
    @get:PropertyName("active")
    @set:PropertyName("active")
    var active: String = "",
    @get:PropertyName("addonitem_name")
    @set:PropertyName("addonitem_name")
    var addonItemName: String = "",
    @get:PropertyName("addonitem_price")
    @set:PropertyName("addonitem_price")
    var addonItemPrice: String = "",
    @get:PropertyName("addonitem_rank")
    @set:PropertyName("addonitem_rank")
    var addonItemRank: String = "",
    @get:PropertyName("attributes")
    @set:PropertyName("attributes")
    var attributes: String = "",
)
