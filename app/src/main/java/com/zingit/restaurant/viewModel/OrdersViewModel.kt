package com.zingit.restaurant.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.zingit.restaurant.models.order.OrdersModel
import com.zingit.restaurant.models.order.SearchState
import com.zingit.restaurant.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject


@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val repository: FirebaseRepository
) : ViewModel() {
    private val TAG = "OrdersViewModel"
    private val _orderActiveData: MutableStateFlow<List<OrdersModel>> = MutableStateFlow(listOf())
    val orderActiveData: StateFlow<List<OrdersModel>> = _orderActiveData

    private val _orderHistoryData: MutableStateFlow<List<OrdersModel>> = MutableStateFlow(listOf())
    val orderHistoryData: StateFlow<List<OrdersModel>> = _orderHistoryData
    var myList: MutableList<OrdersModel> = mutableListOf()
    
    private val _orderPrintNew:MutableStateFlow<OrdersModel> = MutableStateFlow(OrdersModel(customer = null,order = null, orderItem= null, restaurant = null, tax = null,null,null))
    val orderPrintNew:StateFlow<OrdersModel> = _orderPrintNew

    private val _orderSearchData = MutableStateFlow(SearchState())
    val orderSearchData: StateFlow<SearchState> = _orderSearchData


    fun getOrdersData() {
        repository.getOrder().onEach {

            if (it.isNotEmpty()) {
                _orderActiveData.value = it
                Log.d(TAG,"Order Data is :$it")
            }
        }.launchIn(viewModelScope)

    }

    fun printNewOrder(){

        repository.recentOrder().onEach {
            _orderPrintNew.value = it
        }.launchIn(viewModelScope)

    }




//    fun getOrderHistory() {
//        repository.getHistoryOrder().onEach {
//            if (it.isNotEmpty()) {
//                clear()
//                val currentTime = Timestamp.now().seconds
//                it.forEach { order ->
//                    val timeDiff = currentTime  - order.placedTime!!.seconds
//                    if (timeDiff <= 24 * 60 * 60) { // 3 hours in milliseconds
//                        myList.add(order)
//                    }
//
//                }
//                _orderHistoryData.value = myList
//            }
//        }.launchIn(viewModelScope)
//
//    }

    fun clear() {
        myList.clear()
    }


//    fun getOrderSearch(string: String) {
//            repository.getOrderBySearch(string).onEach {
//                when (it) {
//                    is Resource.Loading -> {
//                        _orderSearchData.value = SearchState(isLoading = true)
//                    }
//                    is Resource.Error -> {
//                        _orderSearchData.value = SearchState(error = it.message ?: "")
//                    }
//                    is Resource.Success -> {
////                        _orderSearchData.value = SearchState(data = it.data.size, isLoading = false)
//                        Log.e(TAG, "getOrdersData: ${it.data}")
//
//                    }
//                }
//            }
//
//    }

}


