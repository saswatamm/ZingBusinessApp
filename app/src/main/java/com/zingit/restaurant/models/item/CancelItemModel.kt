package com.zingit.restaurant.models.item

data class CancelItemModel(
    val itemName: String,
    var isChecked: Boolean = false
)
