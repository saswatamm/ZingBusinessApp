package com.zingit.restaurant.models.item

import com.google.firebase.firestore.PropertyName

data class AddonGroupModel(
    @get:PropertyName("active")
    @set:PropertyName("active")
    var active: String = "",
    @get:PropertyName("addongroup_items")
    @set:PropertyName("addongroup_items")
    var addonGroupItems:ArrayList<AddonItemsModel> = arrayListOf() ,
    @get:PropertyName("addongroup_name")
    @set:PropertyName("addongroup_name")
    var addonGroupName:String="" ,
    @get:PropertyName("addongroup_rank")
    @set:PropertyName("addongroup_rank")
    var addonGroupRank:String="" ,
    @get:PropertyName("addongroup_id")
    @set:PropertyName("addongroup_id")
    var addonGroupId:String="" ,
    @get:PropertyName("firebase_restaurant_id")
    @set:PropertyName("firebase_restaurant_id")
    var firebaseRestaurantId:String="" ,
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id:String="" ,
    @get:PropertyName("petpooja_restaurant_id")
    @set:PropertyName("petpooja_restaurant_id")
    var petpoojaRestaurantId:String="" ,

)
