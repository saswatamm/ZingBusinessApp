package com.zingit.restaurant.repository

import android.app.Application
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.zingit.restaurant.models.earning.EarningModel
import com.zingit.restaurant.models.item.AddonGroupModel
import com.zingit.restaurant.models.item.CategoryModel
import com.zingit.restaurant.models.item.ItemMenuModel
import com.zingit.restaurant.models.order.OrdersModel
import com.zingit.restaurant.models.resturant.RestaurantModel
import com.zingit.restaurant.utils.Resource
import com.zingit.restaurant.utils.Utils
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject
import kotlin.math.log

class FirebaseRepository @Inject constructor(private val application: Application) {

    private val TAG = "FirebaseRepository"
    private val fireStoreDatabase = FirebaseFirestore.getInstance()
    fun getRestaurantProfileDate() = flow {
        emit(Resource.Loading())
        try {

            val snapShot =
                fireStoreDatabase.collection("prod_restaurant")
                    .document(Utils.getUserOutletId(application)!!).get()
                    .await()
            if (snapShot.exists()) {
                val restaurantModel: RestaurantModel? =
                    snapShot.toObject(RestaurantModel::class.java)
                Log.e(TAG, "getRestaurantData: ${restaurantModel.toString()}")
                emit(Resource.Success(restaurantModel))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }

    }

    fun getMenuData() = flow {
        Log.e(TAG, "getMenu: ${Utils.getUserOutletId(application)}")
        emit(Resource.Loading())
        try {
            val snapShot = fireStoreDatabase.collection("prod_menu")
                .whereEqualTo("firebase_restaurant_id", Utils.getUserOutletId(application)).get()
                .await()
            Log.e(TAG, "getMenuData: ${snapShot.documents}")
            if (snapShot.documents.isNotEmpty()) {
                Log.d("In FirebaseRepo", "Hi")
                val itemMenuModel: List<ItemMenuModel> =
                    snapShot.toObjects(ItemMenuModel::class.java)
                Log.d(TAG, "Hi :" + itemMenuModel.toString())
                emit(Resource.Success(itemMenuModel))
            }


        } catch (e: Exception) {
            emit(Resource.Error(e.message!!))
        }

    }

    fun getCategoryData() = flow {
        Log.e(TAG, "getCategory of outlet ${Utils.getUserOutletId(application)}")
        emit(Resource.Loading())
        try {
            val snapShot = fireStoreDatabase.collection("prod_category")
                .whereEqualTo("firebase_restaurant_id", Utils.getUserOutletId(application)).get()
                .await()
            Log.e(TAG, "getCateogryData: ${snapShot.documents}")
            if (snapShot.documents.isNotEmpty()) {
                val categoryModel: List<CategoryModel> =
                    snapShot.toObjects(CategoryModel::class.java)
                Log.d(TAG, "Hi :$categoryModel")
                emit(Resource.Success(categoryModel))
            }


        } catch (e: Exception) {
            emit(Resource.Error(e.message!!))
        }
    }

    fun getAddonGroupData(id: String) = flow {
        Log.e(TAG, "getCategory of outlet ${Utils.getUserOutletId(application)}")
        emit(Resource.Loading())
        try {
            val snapShot = fireStoreDatabase.collection("test_addongroups")
                .whereEqualTo("firebase_restaurant_id", Utils.getUserOutletId(application)).get()
                .await()
            Log.e(TAG, "getAddonGroupsData: ${snapShot.documents}")
            if (snapShot.documents.isNotEmpty()) {
                val addonGroupModel: List<AddonGroupModel> =
                    snapShot.toObjects(AddonGroupModel::class.java)
                Log.d(TAG, "Hi :$addonGroupModel")
                emit(Resource.Success(addonGroupModel))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.message!!))
        }
    }

    fun getOrder(): Flow<List<OrdersModel>> = callbackFlow {
        Log.e(TAG, "getOrder: ${Utils.getUserOutletId(application)}")
        val snapShot = fireStoreDatabase.collection("prod_order")
            .whereEqualTo("restaurant.details.restaurant_id", Utils.getMenuSharingCode(application))
            .whereIn("zingDetails.status", mutableListOf("0","1","2"))
            .addSnapshotListener { value, error ->
                if (error != null) {
                    trySend(listOf()).isSuccess
                    return@addSnapshotListener
                }
                if (value != null) {
                    var orderModel = ArrayList<OrdersModel>();
                    val valueIterator = value.documents.iterator()
                    while (valueIterator.hasNext()) {
                        var valueIteratorObject = valueIterator.next();
                        var orderModelObject = valueIteratorObject.toObject(OrdersModel::class.java)
                        if (orderModelObject != null) {
                            orderModelObject.zingDetails?.id = valueIteratorObject.id
                        }
                        if (orderModelObject != null) {
                            orderModel.add(orderModelObject)
                        }
                    }
                    var list = orderModel.sortedByDescending { it.zingDetails?.placedTime }
                    orderModel = ArrayList(list);
                    Log.d("RestaurantProfileViewModel", "orderData is:$orderModel")
                    trySend(orderModel).isSuccess
                }
            }

        awaitClose { snapShot.remove() }

    }

    fun linkedOrderList(date: String) : Flow<List<OrdersModel>> = callbackFlow {
        val snapshot = fireStoreDatabase.collection("prod_order")
            .whereEqualTo("restaurant.details.restaurant_id", Utils.getMenuSharingCode(application))
            .whereEqualTo("order.details.preorder_date", date)
            .whereEqualTo("zingDetails.qrScanned",false)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    trySend(listOf()).isSuccess
                    return@addSnapshotListener
                }
                if (value != null) {
                    var orderModel = ArrayList<OrdersModel>()
                    val valueIterator = value.documents.iterator()
                    while (valueIterator.hasNext()) {
                        var valueIteratorObject = valueIterator.next()
                        var orderModelObject = valueIteratorObject.toObject(OrdersModel::class.java)
                        if (orderModelObject != null) {
                            orderModelObject.zingDetails?.id = valueIteratorObject.id
                        }
                        if (orderModelObject != null) {
                            orderModel.add(orderModelObject)
                        }
                    }
                    var list = orderModel.sortedByDescending { it.zingDetails?.placedTime }
                    orderModel = ArrayList(list);
                    Log.e("LinkedData", "orderData is:$orderModel")
                    trySend(orderModel).isSuccess
                }
            }

        awaitClose { snapshot.remove() }
    }

    fun qrOrderList(date: String): Flow<List<OrdersModel>> = callbackFlow {
        val snapshot = fireStoreDatabase.collection("prod_order")
            .whereEqualTo("restaurant.details.restaurant_id", Utils.getMenuSharingCode(application))
            .whereEqualTo("order.details.preorder_date", date)
            .whereEqualTo("zingDetails.qrScanned",true)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    trySend(listOf()).isSuccess
                    return@addSnapshotListener
                }
                if (value != null) {
                    var orderModel = ArrayList<OrdersModel>()
                    val valueIterator = value.documents.iterator()
                    while (valueIterator.hasNext()) {
                        var valueIteratorObject = valueIterator.next()
                        var orderModelObject = valueIteratorObject.toObject(OrdersModel::class.java)
                        if (orderModelObject != null) {
                            orderModelObject.zingDetails?.id = valueIteratorObject.id
                        }
                        if (orderModelObject != null) {
                            orderModel.add(orderModelObject)
                        }
                    }
                    var list = orderModel.sortedByDescending { it.zingDetails?.placedTime }
                    orderModel = ArrayList(list)
                    Log.d("RestaurantProfileViewModel", "orderData is:$value")
                    trySend(orderModel).isSuccess
                }

            }

        awaitClose { snapshot.remove() }

    }


    fun getEarningDb(date: String): Flow<EarningModel> = callbackFlow {
        Log.e(TAG, "getEarningDb: ${Utils.getUserOutletId(application)}")
        val snapshot = fireStoreDatabase.collection("prod_earnings")
            .whereEqualTo("restaurantID", Utils.getUserOutletId(application))
            .whereEqualTo("created_on", date)//yyyy-mm-dd
            .addSnapshotListener { value, error ->
                if (error != null) {
                    trySend(EarningModel(null, null, null, null, null, null, null, null)).isSuccess
                    return@addSnapshotListener
                }
                if (value != null) {
                    Log.e(TAG, "getEarningDb: $value")
                    for (i in value.documentChanges) {
                        when (i.type) {
                            DocumentChange.Type.ADDED -> {
                                val earningModel: EarningModel =
                                    i.document.toObject(EarningModel::class.java)
                                trySend((earningModel)).isSuccess

                            }

                            DocumentChange.Type.MODIFIED -> {
                                val earningModel: EarningModel =
                                    i.document.toObject(EarningModel::class.java)
                                trySend((earningModel)).isSuccess

                            }

                            else -> {}
                        }
                    }

                }
            }
        awaitClose { snapshot.remove() }

    }

    fun getHistoryOrder(): Flow<List<OrdersModel>> = callbackFlow {
        Log.e(TAG, "getHistoryOrder: ${Utils.getUserOutletId(application)}")
        val snapShot = fireStoreDatabase.collection("prod_order")
            .whereEqualTo("restaurant.details.restaurant_id", Utils.getUserOutletId(application))
            .whereEqualTo("zingDetails.status", "5")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    trySend(listOf()).isSuccess
                    return@addSnapshotListener
                }
                if (value != null) {
                    val orderModel: List<OrdersModel> =
                        value.toObjects(OrdersModel::class.java)
                    Log.d(TAG, "History Order data is" + orderModel.toString())
                    trySend(orderModel)
                }
            }

        awaitClose { snapShot.remove() }

    }

    fun recentOrder(): Flow<OrdersModel> = callbackFlow {
        Log.e(TAG, "getRecent: ${Utils.getUserOutletId(application)}")
        val snapShot = fireStoreDatabase.collection("prod_order")
            .whereEqualTo("restaurant.details.restaurant_id", Utils.getUserOutletId(application))
            .whereEqualTo("status", "0").addSnapshotListener { value, error ->
                if (error != null) {
                    trySend(OrdersModel(null, null, null, null, null, null)).isSuccess
                    return@addSnapshotListener
                }
                if (value != null) {
                    for (i in value.documentChanges) {
                        when (i.type) {
                            DocumentChange.Type.ADDED -> {
                                val orderModel: OrdersModel =
                                    i.document.toObject(OrdersModel::class.java)
                                trySend((orderModel)).isSuccess

                            }

                            DocumentChange.Type.MODIFIED -> {
                                val orderModel: OrdersModel =
                                    i.document.toObject(OrdersModel::class.java)
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