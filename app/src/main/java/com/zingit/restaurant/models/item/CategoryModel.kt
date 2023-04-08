package com.zingit.restaurant.models.item

import com.google.gson.annotations.SerializedName

data class CategoryModel(
    @SerializedName("category")
    val category: String = "",
    @SerializedName("itemImage")
    val itemImage: String = ""
)
