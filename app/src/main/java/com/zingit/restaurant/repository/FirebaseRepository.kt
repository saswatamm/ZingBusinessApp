package com.zingit.restaurant.repository

import android.app.Application
import android.content.Context
import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firestore.v1.StructuredQuery.Order

import com.zingit.restaurant.models.item.ItemMenuModel
import com.zingit.restaurant.models.order.OrderState
import com.zingit.restaurant.models.order.OrdersModel
import com.zingit.restaurant.models.resturant.RestaurantProfileModel
import com.zingit.restaurant.utils.Resource
import com.zingit.restaurant.utils.Utils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseRepository @Inject constructor(private val application:Application){

    private val TAG = "FirebaseRepository"
    private val fireStoreDatabase = FirebaseFirestore.getInstance()
    fun getRestaurantProfileDate() = flow {
        emit(Resource.Loading())
        try {

            val snapShot =
                fireStoreDatabase.collection("outlet").document(Utils.getUserOutletId(application)!!).get()
                    .await()


            if (snapShot.exists()) {
                val restaurantProfileModel: RestaurantProfileModel? =
                    snapShot.toObject(RestaurantProfileModel::class.java)
                Log.e(TAG, "getRestaurantProfileDate: ${restaurantProfileModel.toString()}")
                emit(Resource.Success(restaurantProfileModel!!))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }

    }

    fun getMenuData() = flow {
        Log.e(TAG, "getMenu: ${Utils.getUserOutletId(application)}", )
        emit(Resource.Loading())
        try {
            val snapShot = fireStoreDatabase.collection("item")
                .whereEqualTo("outletID", Utils.getUserOutletId(application)).get().await()
            Log.e(TAG, "getMenuData: ${snapShot.documents}")
            if (snapShot.documents.isNotEmpty()) {
                val itemMenuModel: List<ItemMenuModel> =
                    snapShot.toObjects(ItemMenuModel::class.java)
                emit(Resource.Success(itemMenuModel!!))
            }


        } catch (e: Exception) {
            emit(Resource.Error(e.message!!))
        }


    }


    fun getOrder(): Flow<List<OrdersModel>> = callbackFlow {
        Log.e(TAG, "getOrder: ${Utils.getUserOutletId(application)}", )
        val snapShot = fireStoreDatabase.collection("payment")
            .whereEqualTo("outletID", Utils.getUserOutletId(application)).whereGreaterThan("statusCode", 0)
            .whereLessThan("statusCode", 3).addSnapshotListener { value, error ->
                if (error != null) {
                    trySend(listOf()).isSuccess
                    return@addSnapshotListener
                }
                if (value != null) {


                    var orderModel: List<OrdersModel> =
                        value.toObjects(OrdersModel::class.java)

                    orderModel = orderModel.sortedByDescending { it.orderNo } //Orders sorted in descending order



                    trySend(orderModel).isSuccess
                }
            }

        awaitClose { snapShot.remove() }


    }





    fun getHistoryOrder(): Flow<List<OrdersModel>> = callbackFlow {
        Log.e(TAG, "getHistoryOrder: ${Utils.getUserOutletId(application)}", )
        val snapShot = fireStoreDatabase.collection("payment")
            .whereEqualTo("outletID", Utils.getUserOutletId(application)).whereEqualTo("statusCode", 3)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    trySend(listOf()).isSuccess
                    return@addSnapshotListener
                }
                if (value != null) {
                    val orderModel: List<OrdersModel> =
                        value.toObjects(OrdersModel::class.java)
                    trySend(orderModel).isSuccess
                }
            }

        awaitClose { snapShot.remove() }


    }

    fun recentOrder(): Flow<OrdersModel> = callbackFlow {
        Log.e(TAG, "getRecent: ${Utils.getUserOutletId(application)}", )
        val snapShot = fireStoreDatabase.collection("payment")
            .whereEqualTo("outletID", Utils.getUserOutletId(application)).whereEqualTo("statusCode",1).addSnapshotListener { value, error ->
                if (error != null) {
                    trySend(OrdersModel()).isSuccess
                    return@addSnapshotListener
                }
                if (value != null) {
                    for(i in  value.documentChanges){
                        when(i.type){
                            DocumentChange.Type.ADDED->{
                                val orderModel: OrdersModel = i.document.toObject(OrdersModel::class.java)
                                trySend((orderModel)).isSuccess

                            }
                            DocumentChange.Type.MODIFIED->{
                                val orderModel: OrdersModel = i.document.toObject(OrdersModel::class.java)
                                trySend((orderModel)).isSuccess

                            }
                            else -> {}
                        }

                    }

                }
            }

        awaitClose { snapShot.remove() }


    }

}