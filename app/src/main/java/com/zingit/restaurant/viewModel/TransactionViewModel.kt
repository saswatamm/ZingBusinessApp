package com.zingit.restaurant.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zingit.restaurant.models.earning.EarningModel
import com.zingit.restaurant.models.order.OrdersModel
import com.zingit.restaurant.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject
constructor(
    private var firebaseRepository: FirebaseRepository,
): ViewModel() {
    private  val TAG = "TransactionViewModel"
    private val _earningData = MutableStateFlow(EarningModel())
    val earningData: StateFlow<EarningModel> = _earningData

    private val _orderActiveData: MutableStateFlow<List<OrdersModel>> = MutableStateFlow(listOf())
    val orderActiveData: StateFlow<List<OrdersModel>> = _orderActiveData

    private val _orderLinkedData: MutableStateFlow<List<OrdersModel>> = MutableStateFlow(listOf())
    val orderLinkedData: StateFlow<List<OrdersModel>> = _orderLinkedData



    fun getEarningData(date: String) {
       firebaseRepository.getEarningDb(date).onEach {

           if (it!=null) {
               _earningData.value = it
               Log.d(TAG,"Order Data is :$it")
           }
       }.launchIn(viewModelScope)
    }

    fun getOrdersData(date: String) {
        firebaseRepository.qrOrderList(date).onEach {

            if (it.isNotEmpty()) {
                _orderActiveData.value = it
                Log.d(TAG,"Order Data is :$it")
            }
        }.launchIn(viewModelScope)

    }

    fun getLinkedOrdersData(date: String) {
        firebaseRepository.linkedOrderList(date).onEach {
            if (it.isNotEmpty()) {
                _orderLinkedData.value = it
                Log.d(TAG,"Order Data is :$it")
            }
        }.launchIn(viewModelScope)

    }
}