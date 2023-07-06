package com.zingit.restaurant.models.item

import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.SerializedName

data class VariationsModel(
    @get:PropertyName("active")
    @set:PropertyName("active")
    var active: String = "",
    @get:PropertyName("addon")
    @set:PropertyName("addon")
    var addOn: ArrayList<AddOnModel> = arrayListOf(),
    @get:PropertyName("groupname")
    @set:PropertyName("groupname")
    var groupName: String = "",
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: String = "",
    @get:PropertyName("item_packingcharges")
    @set:PropertyName("item_packingcharges")
    var itemPackingCharges: String = "",
    @get:PropertyName("name")
    @set:PropertyName("name")
    var name: String = "",
    @SerializedName("price")
    val price: String = "",
    @SerializedName("variationallowaddon")
    val variationAllowAddOn: Int = 0,
    @get:PropertyName("variationid")
    @set:PropertyName("variationid")
    var variationId: String = "",
    @get:PropertyName("variationrank")
    @set:PropertyName("variationrank")
    var variationRank: String = "",
    )
