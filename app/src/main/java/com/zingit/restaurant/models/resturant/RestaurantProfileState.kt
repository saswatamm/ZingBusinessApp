package com.zingit.restaurant.models.resturant

data class RestaurantProfileState(
    val data: RestaurantProfileModel? = null, val error: String = "", val isLoading: Boolean = false
)