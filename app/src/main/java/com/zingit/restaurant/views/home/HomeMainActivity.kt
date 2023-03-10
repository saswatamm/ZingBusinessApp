package com.zingit.restaurant.views.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.zingit.restaurant.R
import com.zingit.restaurant.databinding.ActivityHomeMainBinding
import com.zingit.restaurant.databinding.ActivityMainBinding

class HomeMainActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeMainBinding
    lateinit var navController : NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_home_main)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

    }
}