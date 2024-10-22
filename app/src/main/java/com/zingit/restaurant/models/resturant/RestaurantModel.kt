package com.zingit.restaurant.models.resturant

import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.SerializedName

data class RestaurantModel(
    @SerializedName("active")
    val active: String="",
    @SerializedName("address")
    val address: String="",
    @get:PropertyName("calculate_tax_on_packaging")
    @set:PropertyName("calculate_tax_on_packaging")
    var calculateTaxOnPkg: Int = 0,
    @SerializedName("campusId")
    var campusId: ArrayList<String> = arrayListOf(),
//    @SerializedName("city")
//    val city: String ="",
    @SerializedName("contact")
    val contact: String ="",
    @SerializedName("country")
    val country: String ="",
    @get:PropertyName("firebase_restaurant_id")
    @set:PropertyName("firebase_restaurant_id")
    var firebaseRestaurantId: String ="",
    @SerializedName("id")
    val id: String ="",
    @SerializedName("image")
    val image: ArrayList<String> = arrayListOf(),
    @get:PropertyName("is_petpooja_integrated")
    @set:PropertyName("is_petpooja_integrated")
    var isPetpoojaIntegrated:  Boolean =false,
    @SerializedName("landmark")
    val landmark: String="",
    @SerializedName("latitude")
    val latitude: Double=0.0,
    @SerializedName("longitude")
    val longitude: Double=0.0,
    @get:PropertyName("menusharingcode")
    @set:PropertyName("menusharingcode") //Check
    var menuSharingCode: String ="",
    @get:PropertyName("minimum_order_amount")
    @set:PropertyName("minimum_order_amount")
    var minimumOrderAmt: String ="",
    @get:PropertyName("packaging_applicable_on")
    @set:PropertyName("packaging_applicable_on")
    var pkgApplicableOn: String ="",
    @get:PropertyName("packaging_charge")
    @set:PropertyName("packaging_charge")
    var pkgCharge: String ="",
    @get:PropertyName("packaging_charge_type")
    @set:PropertyName("packaging_charge_type")
    var pkgChargeType: String ="",
    @get:PropertyName("petpooja_restaurant_id")
    @set:PropertyName("petpooja_restaurant_id")
    var petpoojaRestaurantId: String ="",
    @get:PropertyName("restaurant_name")
    @set:PropertyName("restaurant_name")
    var restaurantName: String ="",
//    var taxOnSc: String ="",
    @get:PropertyName("zingTime")
    @set:PropertyName("zingTime")
    var zingTime: Int=0,
//    @get:PropertyName("orderNumberRange")
//    @set:PropertyName("orderNumberRange")
//    var orderNumberRange: Double=0.0,
    @get:PropertyName("cuisine")
    @set:PropertyName("cuisine")
    var cuisine: ArrayList<String> = arrayListOf(),

    )
