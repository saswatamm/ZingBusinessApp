package com.zingit.restaurant.models.item

data class CancelItemModel(
    val itemName: String,
    val itemId :String,
    var isChecked: Boolean = false
)
