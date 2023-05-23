package com.zingit.restaurant.models.item

import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.SerializedName

data class CategoryModel(
    @get:PropertyName("category_name")
    @set:PropertyName("firebase_addon_group_id")
    var categoryName: String = "",
    @get:PropertyName("item_image_url")
    @set:PropertyName("item_image_url")
    var itemImgUrl: String = ""
)
