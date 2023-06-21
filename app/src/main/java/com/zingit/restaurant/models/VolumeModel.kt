package com.zingit.restaurant.models

import com.google.gson.annotations.SerializedName

data class VolumeModel(

val orderID: String,
val success: Boolean,
val amount : String

)
