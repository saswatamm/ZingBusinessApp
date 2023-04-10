package com.zingit.restaurant.models.order

import com.zingit.restaurant.models.item.ItemMenuModel
import com.zingit.restaurant.models.resturant.RestaurantProfileModel

data class OrderState(val data: List<OrdersModel>? = null, val error: String = "", val isLoading: Boolean = false)
