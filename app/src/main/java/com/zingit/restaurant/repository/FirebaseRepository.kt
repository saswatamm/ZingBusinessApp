package com.zingit.restaurant.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.zingit.restaurant.models.PaymentModel
import com.zingit.restaurant.models.item.ItemMenuModel
import com.zingit.restaurant.models.order.OrdersModel
import com.zingit.restaurant.models.resturant.RestaurantProfileModel
import com.zingit.restaurant.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseRepository @Inject constructor() {

    private val TAG = "FirebaseRepository"
    private val fireStoreDatabase = FirebaseFirestore.getInstance()
    fun getRestaurantProfileDate() = flow {
        emit(Resource.Loading())
        try {
            val snapShot =
                fireStoreDatabase.collection("outlet").document("1cLAN8pKJcuyIML9g8Uz").get()
                    .await()


            if (snapShot.exists()) {
                val restaurantProfileModel: RestaurantProfileModel? =
                    snapShot.toObject(RestaurantProfileModel::class.java)
                Log.e(TAG, "getRestaurantProfileDate: ${restaurantProfileModel.toString()}")
                emit(Resource.Success(restaurantProfileModel!!))
            }


        } catch (e: Exception) {
            emit(Resource.Error(e.message!!))
        }


    }

    fun getMenuData() = flow {
        emit(Resource.Loading())
        try {
            val snapShot = fireStoreDatabase.collection("item")
                .whereEqualTo("outletID", "1cLAN8pKJcuyIML9g8Uz").get().await()
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


    fun getOrder() = flow {
        emit(Resource.Loading())
        try {
            val snapShot = fireStoreDatabase.collection("payment")
                .whereEqualTo("outletID", "9i1Q3aRU8AiH0dUAZjko").whereGreaterThan("statusCode",0).whereLessThan("statusCode",4).get().await()
            Log.e(TAG, "orderData: ${snapShot.documents}")
            if (snapShot.documents.isNotEmpty()) {
                val orderModel: List<OrdersModel> =
                    snapShot.toObjects(OrdersModel::class.java)
                emit(Resource.Success(orderModel!!))

            }


        } catch (e: Exception) {
            emit(Resource.Error(e.message!!))
        }
    }

}