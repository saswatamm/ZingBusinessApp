package com.zingit.restaurant.models.item

import com.google.gson.annotations.SerializedName

data class VariationsModel(
    @SerializedName("active")
    val active: String = "",
    @SerializedName("addon")
    val addOn: ArrayList<AddOnModel> = arrayListOf(),
    @SerializedName("groupname")
    val groupName: String = "",
    @SerializedName("id")
    val id: String = "",
    @SerializedName("item_packingcharges")
    val itemPackingCharges: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("price")
    val price: String = "",
    @SerializedName("variationallowaddon")
    val variationAllowAddOn: String = "",
    @SerializedName("variationid")
    val variationId: String = "",
    @SerializedName("variationrank")
    val variationRank: String = "",
    )
