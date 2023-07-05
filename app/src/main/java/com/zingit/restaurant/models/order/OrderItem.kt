package com.zingit.restaurant.models.order

import com.google.firebase.firestore.PropertyName


data class OrderItem(
    @get:PropertyName("details")
    @set:PropertyName("details")
    var details :ArrayList<OrderItemDetails> = arrayListOf()
)
data class OrderItemDetails(
    @get:PropertyName("description")
    @set:PropertyName("description")
    var decsription: String = "",
    @get:PropertyName("final_price")
    @set:PropertyName("final_price")
    var final_price: String = "",
    @get:PropertyName("gst_liability")
    @set:PropertyName("gst_liability")
    var gstLiability: String = "",
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: String = "",
    @get:PropertyName("item_discount")
    @set:PropertyName("item_discount")
    var itemDiscount: String = "",
    @get:PropertyName("name")
    @set:PropertyName("name")
    var name: String = "",
    @get:PropertyName("price")
    @set:PropertyName("price")
    var price: String = "",
    @get:PropertyName("quantity")
    @set:PropertyName("quantity")
    var quantity: String = "",
    @get:PropertyName("variation_id")
    @set:PropertyName("variation_id")
    var variationId: String = "",
    @get:PropertyName("variation_name")
    @set:PropertyName("variation_name")
    var variationName: String = "",
//    @get:PropertyName("item_tax")
//    @set:PropertyName("item_tax")
//    var itemTax: ArrayList<OrderItemTax> = arrayListOf(),
    @get:PropertyName("AddonItem")
    @set:PropertyName("AddonItem")
    var addonItem: AddonItemDetails?=AddonItemDetails(),
)

data class OrderItemTax(
    @get:PropertyName("amount")
    @set:PropertyName("amount")
    var amount: String = "",
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: String = "",
    @get:PropertyName("name")
    @set:PropertyName("name")
    var name: String = "",
)
data class AddonItemDetails(
    @get:PropertyName("details")
    @set:PropertyName("details")
    var details: ArrayList<AddonItem> = arrayListOf(),
)
data class AddonItem(
    @get:PropertyName("group_id")
    @set:PropertyName("group_id")
    var groupId: String = "",
    @get:PropertyName("group_name")
    @set:PropertyName("group_name")
    var groupName: String = "",
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: String = "",
    @get:PropertyName("name")
    @set:PropertyName("name")
    var name: String = "",
    @get:PropertyName("price")
    @set:PropertyName("price")
    var price: String = "",
    @get:PropertyName("quantity")
    @set:PropertyName("quantity")
    var quantity: String = "",
)