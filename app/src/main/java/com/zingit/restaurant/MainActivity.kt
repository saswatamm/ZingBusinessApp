package com.zingit.restaurant

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.zingit.restaurant.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var navController : NavController
    private val REQUEST_CODE_NOTIFICATION_PERMISSION = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        if (NotificationManagerCompat.from(applicationContext).areNotificationsEnabled()) {
            // Notifications are already enabled
        } else {
            // Notification permission has not been granted yet, request it
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS, android.Manifest.permission.ACCESS_NOTIFICATION_POLICY),
                REQUEST_CODE_NOTIFICATION_PERMISSION)
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_NOTIFICATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, enable notifications
            } else {

            }
        }
    }
}