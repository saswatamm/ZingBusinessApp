package com.zingit.restaurant.viewModel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp

import com.zingit.restaurant.models.order.OrdersModel
import com.zingit.restaurant.models.order.SearchState
import com.zingit.restaurant.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject


@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val repository: FirebaseRepository
) : ViewModel() {
    private val TAG = "OrdersViewModel"
    private val _orderActiveData: MutableStateFlow<List<OrdersModel>> = MutableStateFlow(listOf())
    val orderActiveData: StateFlow<List<OrdersModel>> = _orderActiveData
    private val _loading : MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loading : StateFlow<Boolean> = _loading

    private val _orderHistoryData: MutableStateFlow<List<OrdersModel>> = MutableStateFlow(listOf())
    val orderHistoryData: StateFlow<List<OrdersModel>> = _orderHistoryData
    var myList: MutableList<OrdersModel> = mutableListOf()
    
    private val _orderPrintNew:MutableStateFlow<OrdersModel> = MutableStateFlow(OrdersModel(customer = null,order = null, orderItem= null, restaurant = null, tax = null,null))
    val orderPrintNew:StateFlow<OrdersModel> = _orderPrintNew

    private val _orderSearchData = MutableStateFlow(SearchState())
    val orderSearchData: StateFlow<SearchState> = _orderSearchData


    fun getOrdersData() {
        _orderActiveData.value = listOf()
        _loading.value = true
        repository.getOrder().onEach {
            if (it.isNotEmpty()) {
                _loading.value = false
                _orderActiveData.value = it
                Log.d(TAG,"Order Data is :$it")
            }else{
                _loading.value = false
            }
        }.launchIn(viewModelScope)

    }

    fun printNewOrder(){

        repository.recentOrder().onEach {
            _orderPrintNew.value = it
        }.launchIn(viewModelScope)

    }




    @RequiresApi(Build.VERSION_CODES.O)
    fun getOrderHistory() {
        repository.getHistoryOrder().onEach {
            if (it.isNotEmpty()) {
                clear()
                it.forEach { orderModel ->
                    val currentTime = Timestamp.now().seconds
                    val time=orderModel.order?.details!!.createdOn.substringAfter(" ")
                    val date=orderModel.order?.details!!.createdOn.substringBefore(" ")
                    val dateTime=date+"T"+time
                    val ldt = LocalDateTime.parse(dateTime)
                    val seconds=ldt.atZone(ZoneOffset.UTC).toEpochSecond()
                    val timeDiff = currentTime  - seconds
                    if (timeDiff <= 24 * 60 * 60) {
                        myList.add(orderModel)
                    }

//CN

                }
                _orderHistoryData.value = myList
            }
        }.launchIn(viewModelScope)

    }

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


