package com.zingit.restaurant.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.google.firebase.firestore.FirebaseFirestore
import com.zingit.restaurant.R
import com.zingit.restaurant.databinding.ActivityFirebaseNotificationActionBinding
import com.zingit.restaurant.models.PaymentModel
import com.zingit.restaurant.viewModel.SignUpLoginViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FirebaseNotificationActionActivity : AppCompatActivity() {
    private val viewModel: SignUpLoginViewModel by viewModels()
    lateinit var binding: ActivityFirebaseNotificationActionBinding
    lateinit var firestore: FirebaseFirestore
    private val TAG = "FirebaseNotificationActionActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_firebase_notification_action)
        firestore = FirebaseFirestore.getInstance()
        val count = intent.getStringExtra("id")
        binding.apply {

            firestore.collection("payment").document(count!!).get().addOnSuccessListener {
                Log.e(TAG, "onCreate: ${it.data}", )
                it.toObject(PaymentModel::class.java)?.let { it1 -> Log.e(TAG, "qwerty: ${it1}")}


            }
          





        }
    }
}