package com.zingit.restaurant.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.zingit.restaurant.models.PaymentModel
import com.zingit.restaurant.models.resturant.RestaurantProfileModel
import com.zingit.restaurant.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseRepository @Inject constructor() {

    private  val TAG = "FirebaseRepository"
    private val fireStoreDatabase = FirebaseFirestore.getInstance()
    lateinit var restaurantProfileModel: RestaurantProfileModel

    fun getRestaurantProfileDate()  = flow {
        emit(Resource.Loading())
        try{
            val snapShot = fireStoreDatabase.collection("outlet").document("1cLAN8pKJcuyIML9g8Uz").get().await()


            if(snapShot.exists()){


               val  restaurantProfileModel :RestaurantProfileModel? = snapShot.toObject(RestaurantProfileModel::class.java)
                Log.e(TAG, "getRestaurantProfileDate: ${restaurantProfileModel.toString()}", )
                emit(Resource.Success(restaurantProfileModel!!))
            }


        }catch (e:Exception){
            emit(Resource.Error(e.message!!))
        }
    }



}