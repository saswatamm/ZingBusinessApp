package com.zingit.restaurant.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firestore.v1.StructuredQuery.Order
import com.zingit.restaurant.models.item.CategoryModel
import com.zingit.restaurant.models.item.CategoryState
import com.zingit.restaurant.models.item.ItemMenuState
import com.zingit.restaurant.models.order.OrderState
import com.zingit.restaurant.models.order.OrdersModel
import com.zingit.restaurant.repository.FirebaseRepository
import com.zingit.restaurant.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject



@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val repository: FirebaseRepository
) : ViewModel(){
    private  val TAG = "OrdersViewModel"
    private val _orderActiveData = MutableStateFlow(OrderState())
    val orderActiveData: StateFlow<OrderState> = _orderActiveData

    private val _orderHistoryData = MutableStateFlow(OrderState())
    val orderHistoryData: StateFlow<OrderState> = _orderHistoryData
    var myList: MutableList<OrdersModel> = mutableListOf<OrdersModel>()


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
                    _orderActiveData.value = OrderState(data = it.data, isLoading = false)

                    Log.e(TAG, "getOrdersData: ${it.data}", )
                }
            }
        }.launchIn(viewModelScope)
    }


    fun getOrderHistory(){
        repository.getHistoryOrder().onEach {
            when (it) {
                is Resource.Loading -> {
                    _orderHistoryData.value = OrderState(isLoading = true)
                }
                is Resource.Error -> {
                    _orderHistoryData.value = OrderState(error = it.message ?: "")
                }
                is Resource.Success -> {
                    clear()
                    val currentTime = Timestamp.now().seconds
                    it.data?.forEach { order ->
                        val timeDiff = currentTime  - order.placedTime!!.seconds
                        if (timeDiff <= 3 * 60 * 60) { // 3 hours in milliseconds
                            myList.add(order)
                        }

                    }
                    _orderHistoryData.value = OrderState(data = myList, isLoading = false)
                    Log.e(TAG, "histroy: ${it.data}", )
                }
            }
        }.launchIn(viewModelScope)

    }
    fun clear (){
        myList.clear()
    }

}


