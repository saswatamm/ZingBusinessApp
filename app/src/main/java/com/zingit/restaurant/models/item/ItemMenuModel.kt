package com.zingit.restaurant.models.item

import com.google.gson.annotations.SerializedName

data class ItemMenuModel(
    @SerializedName("availableOrNot")
    @field:JvmField
    val availableOrNot: Boolean = false,
    @SerializedName("category")
    val category: String = "",
    @SerializedName("itemImage")
    val itemImage: String = "",
    @SerializedName("maxAllowed")
    val maxAllowed: Int = 0,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("price")
    val price: Int = 0,
    @SerializedName("takeAwayCharges")
    val takeAwayCharges: Int = 0,
    @SerializedName("vegOrNot")
    @field:JvmField
    val vegOrNot: Boolean = false

)
