package com.zingit.restaurant.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.util.toAndroidPair
import androidx.databinding.DataBindingUtil
import com.google.firebase.firestore.FirebaseFirestore
import com.zingit.restaurant.R
import com.zingit.restaurant.adapter.ItemAdapter
import com.zingit.restaurant.databinding.ActivityFirebaseNotificationActionBinding
import com.zingit.restaurant.models.OrderItem
import com.zingit.restaurant.models.PaymentModel
import com.zingit.restaurant.models.WhatsappRequestModel
import com.zingit.restaurant.viewModel.SignUpLoginViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FirebaseNotificationActionActivity : AppCompatActivity() {
    private val viewModel: SignUpLoginViewModel by viewModels()
    lateinit var binding: ActivityFirebaseNotificationActionBinding
    lateinit var firestore: FirebaseFirestore
    lateinit var itemAdapter: ItemAdapter
    var orderItems = ArrayList<OrderItem>()
    var hashMap: HashMap<String, Int> = HashMap()
    private val TAG = "FirebaseNotificationActionActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_firebase_notification_action)
        firestore = FirebaseFirestore.getInstance()
        val count = intent.getStringExtra("id")
        binding.apply {


            firestore.collection("payment").document(count!!).get().addOnSuccessListener {
                Log.e(TAG, "onCreate: ${it.data}")
                orderItems.clear()
                it.toObject(PaymentModel::class.java)?.let { it1 ->
                    Log.e(TAG, "qwerty: ${it1}")
                    paymentModel = it1
                    firestore.collection("outlet").document(it1.outletID).get().addOnSuccessListener { it2 ->

                        oderId.text = "#${paymentModel?.orderNo}"
                        fromOrder.text = "From: ${paymentModel?.userName} at "
                        itemAdapter = ItemAdapter(context = applicationContext)
                        itemsList.adapter = itemAdapter
                        it.data?.mapValues { it.value }?.forEach { (key, value) ->
                            Log.e(TAG, "ttt: $key $value")
                            if (key == "orderItems") {
                                Log.e(TAG, "ttt: $key $value")
                                var data = value as ArrayList<OrderItem>
                                for (i in 0 until data.size) {
                                    val map = data[i] as HashMap<String, String>
                                    val itemID = map["itemID"].toString()
                                    val itemTotal = map["itemTotal"].toString()
                                    val itemName = map["itemName"].toString()
                                    val itemQuantity = map["itemQuantity"].toString()
                                    val itemImage = map["itemImage"].toString()

                                    Log.e(TAG, "hashMap: ${hashMap.toString()}")
                                    orderItems.add(
                                        OrderItem(
                                            itemID,
                                            itemImage,
                                            itemName,
                                            itemQuantity.toLong(),
                                            itemTotal.toLong()
                                        )
                                    )
                                    Log.e(TAG, "list:${orderItems}")
                                    val mapped =
                                        hashMapOf(
                                            Pair(
                                                first = itemName,
                                                second = itemQuantity.toInt()
                                            )
                                        )
                                    hashMap.putAll(mapped)



                                    Log.e(TAG, "hash: ${hashMap}")
                                    itemsList.adapter = itemAdapter
                                    itemAdapter.submitList(orderItems)
                                }


                            }


                        }
                        orderReady.setOnClickListener {
                            viewModel.whatsappToUser(
                                WhatsappRequestModel(
                                    "https://msrit.zingnow.in/",
                                    it1.userName,
                                    it1.orderNo,
                                    hashMap,
                                    "+918305809059", it2.get("description").toString(),
                                    "order_accepted_campaign","15"
                                )
                            )

                        }

                        reject.setOnClickListener {
                            viewModel.whatsappToUser(
                                WhatsappRequestModel(
                                    "https://msrit.zingnow.in/",
                                    it1.userName,
                                    it1.orderNo,
                                    hashMap,
                                    "+918305809059", it2.get("description").toString(),
                                    "order_declined_campaign",
                                    "15"
                                )
                            )

                        }


                    }


                }
            }
        }
    }
}