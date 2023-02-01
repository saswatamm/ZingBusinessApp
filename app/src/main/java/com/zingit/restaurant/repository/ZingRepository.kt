package com.zingit.restaurant.repository

import android.content.Context
import com.zingit.restaurant.network.ApiEndPoints
import com.zingit.restaurant.network.ApiUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ZingRepository @Inject constructor(val apiInterFace: ApiEndPoints,private val apiUtils: ApiUtils, @ApplicationContext private val context: Context) {

}