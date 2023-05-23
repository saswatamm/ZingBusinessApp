package com.zingit.restaurant.models.resturant

data class RestaurantProfileState(
    val data: RestaurantModel? = null, val error: String = "", val isLoading: Boolean = false
)