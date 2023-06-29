package com.zingit.restaurant.models.item

import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.SerializedName

data class ItemMenuModel(
    @SerializedName("active")
    var active: String="",
    @get:PropertyName("addons")
    @set:PropertyName("addons")
    var addons: ArrayList<AddOnModel> = arrayListOf(),
    @get:PropertyName("category_name")
    @set:PropertyName("category_name")
    var categoryName: String = "",
    @get:PropertyName("firebase_categoryid")
    @set:PropertyName("firebase_categoryid")
    var firebaseCategoryId: String = "",
    @get:PropertyName("firebase_item_tax")
    @set:PropertyName("firebase_item_tax")
    var firebaseItemTax: ArrayList<String> = arrayListOf(),     //check if it is map or it causes error
    @get:PropertyName("firebase_restaurant_id")
    @set:PropertyName("firebase_restaurant_id")
    var firebaseRestaurantId: String = "",
    @get:PropertyName("id")
    @set:PropertyName("id")
    var Id: String = "",
    @get:PropertyName("ignore_discounts")
    @set:PropertyName("ignore_discounts")
    var ignoreDiscounts: String = "",
    @get:PropertyName("ignore_taxes")
    @set:PropertyName("ignore_taxes")
    var ignoreTaxes: String = "",
    @get:PropertyName("in_stock")
    @set:PropertyName("in_stock")
    var inStock: String = "",
    @get:PropertyName("item_attribute")
    @set:PropertyName("item_attribute")
    var itemAttribute: String = "",
    @get:PropertyName("item_favorite")
    @set:PropertyName("item_favorite")
    var itemFavorite: String = "",
    @get:PropertyName("item_image_url")
    @set:PropertyName("item_image_url")
    var itemImgUrl: String = "",
    @get:PropertyName("item_ordertype")
    @set:PropertyName("item_ordertype")
    var itemOrderType: String = "",
    @get:PropertyName("item_packingcharges")
    @set:PropertyName("item_packingcharges")
    var itemPackingCharges: String = "",
    @get:PropertyName("itemaddonbasedon")
    @set:PropertyName("itemaddonbasedon")
    var itemAddOnBasedOn: String = "",
    @get:PropertyName("itemallowaddon")
    @set:PropertyName("itemallowaddon")
    var itemAllowAddOn: String = "",
    @get:PropertyName("itemallowvariation")
    @set:PropertyName("itemallowvariation")
    var itemAllowVariation: String = "",
    @get:PropertyName("itemdescription")
    @set:PropertyName("itemdescription")
    var itemDescription: String = "",
    @get:PropertyName("itemid")
    @set:PropertyName("itemid")
    var itemId: String = "",
    @get:PropertyName("itemname")
    @set:PropertyName("itemname")
    var itemName: String = "",
    @get:PropertyName("itemrank")
    @set:PropertyName("itemrank")
    var itemRank: String = "",
    @get:PropertyName("minimumpreparationtime")
    @set:PropertyName("minimumpreparationtime")
    var minPrepTime: String = "",
    @get:PropertyName("petpooja_category_id")
    @set:PropertyName("petpooja_category_id")
    var petpoojaCategoryId: String = "",
    @get:PropertyName("petpooja_restaurant_id")
    @set:PropertyName("petpooja_restaurant_id")
    var petpoojaRestaurantId: String = "",
    @get:PropertyName("price")
    @set:PropertyName("price")
    var price: String = "",
    @get:PropertyName("variation_groupname")
    @set:PropertyName("variation_groupname")
    var variationGroupName: String = "",
    @get:PropertyName("variations")
    @set:PropertyName("variations")
    var variations: ArrayList<VariationsModel> = arrayListOf()         //check if it is map or it causes error
//    @SerializedName("availableOrNot")
//    @field:JvmField
//    val availableOrNot: Boolean = false,
//    @SerializedName("category")
//    val category: String = "",
//    @SerializedName("itemImage")
//    val itemImage: String = "",
//    @SerializedName("maxAllowed")
//    val maxAllowed: Int = 0,
//    @SerializedName("name")
//    val name: String = "",
//    @SerializedName("price")
//    val price: Int = 0,
//    @SerializedName("takeAwayCharges")
//    val takeAwayCharges: Int = 0,
//    @SerializedName("vegOrNot")
//    @field:JvmField
//    val vegOrNot: Boolean = false,
//    @SerializedName("id")
//    val id: String = ""

)
