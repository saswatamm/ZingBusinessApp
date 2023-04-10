package com.zingit.restaurant.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zingit.restaurant.models.item.CategoryModel
import com.zingit.restaurant.models.item.CategoryState
import com.zingit.restaurant.models.item.ItemMenuState
import com.zingit.restaurant.models.order.OrderState
import com.zingit.restaurant.repository.FirebaseRepository
import com.zingit.restaurant.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject



@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val repository: FirebaseRepository
) : ViewModel(){
    private  val TAG = "OrdersViewModel"
    private val _orderActiveData = MutableStateFlow(OrderState())
    val orderActiveData: StateFlow<OrderState> = _orderActiveData

//    private val _categoryData = MutableStateFlow(CategoryState())
//    val categoryData: StateFlow<CategoryState> = _categoryData
//    var tempList:MutableList<CategoryModel> = mutableListOf()

    fun getOrdersData() {
        repository.getOrder().onEach {
            when (it) {
                is Resource.Loading -> {
                    _orderActiveData.value = OrderState(isLoading = true)
                }
                is Resource.Error -> {
                    _orderActiveData.value = OrderState(error = it.message ?: "")
                }
                is Resource.Success -> {
                    _orderActiveData.value = OrderState(data = it.data)
                    Log.e(TAG, "getOrdersData: ${it.data}", )
                }
            }
        }.launchIn(viewModelScope)
    }
}