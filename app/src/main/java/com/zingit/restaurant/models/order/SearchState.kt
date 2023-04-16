package com.zingit.restaurant.models.order

data class SearchState(val data: OrdersModel? = null, val error: String = "", val isLoading: Boolean = false)