package com.zingit.restaurant.models.item

data class ItemMenuState(
    val data: List<ItemMenuModel>? = null, val error: String = "", val isLoading: Boolean = false
)