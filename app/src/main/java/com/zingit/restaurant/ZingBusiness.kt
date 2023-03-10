package com.zingit.restaurant

import android.app.Application
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class ZingBusiness: Application() {


    companion object{
        lateinit var firebaseToken:String


    }


}