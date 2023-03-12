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
import com.zingit.restaurant.viewModel.SignUpLoginViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FirebaseNotificationActionActivity : AppCompatActivity() {
    private val viewModel: SignUpLoginViewModel by viewModels()
    lateinit var binding: ActivityFirebaseNotificationActionBinding
    private val TAG = "FirebaseNotificationActionActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_firebase_notification_action)
        val count = intent.getStringExtra("id")
        binding.apply {
           viewModel.getDocumentIdData(applicationContext,count!!)
        }
    }
}