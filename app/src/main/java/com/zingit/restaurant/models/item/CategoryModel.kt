package com.zingit.restaurant.models.item

import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.SerializedName

data class CategoryModel(
    @get:PropertyName("categoryname")
    @set:PropertyName("categoryname")
    var categoryName: String = "",
    @get:PropertyName("category_image_url")
    @set:PropertyName("category_image_url")
    var catImgUrl: String = "",
    @get:PropertyName("active")
    @set:PropertyName("active")
    var active: String = "",
    @get:PropertyName("category_rank")
    @set:PropertyName("category_rank")
    var categoryRank: String = "",
    @get:PropertyName("categoryid")
    @set:PropertyName("categoryid")
    var categoryId: String = "",
    @get:PropertyName("category_timings")
    @set:PropertyName("category_timings")
    var categorytimings: String = "",
    @get:PropertyName("firebase_restaurant_id")
    @set:PropertyName("firebase_restaurant_id")
    var firebaseRestaurantId: String = "",
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: String = "",
    @get:PropertyName("parent_category_id")
    @set:PropertyName("parent_category_id")
    var parentCategoryId: String = "",
    @get:PropertyName("petpooja_restaurant_id")
    @set:PropertyName("petpooja_restaurant_id")
    var petpoojaRestaurantId: String = "",

)
