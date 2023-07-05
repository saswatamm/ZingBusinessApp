package com.zingit.restaurant.models.item

data class CategoryState(
    val data: List<CategoryModel>? = null,val error: String = "", val isLoading: Boolean = false
)