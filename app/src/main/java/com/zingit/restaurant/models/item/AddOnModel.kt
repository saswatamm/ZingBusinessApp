package com.zingit.restaurant.models.item

import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.SerializedName

data class AddOnModel(
    @get:PropertyName("addon_item_selection_max")
    @set:PropertyName("addon_item_selection_max")
    var addonItemSelectMax: String = "",
    @get:PropertyName("addon_item_selection_min")
    @set:PropertyName("addon_item_selection_min")
    var addonItemSelectMin: String = "",
    @get:PropertyName("firebase_addon_group_id")
    @set:PropertyName("firebase_addon_group_id")
    var firebaseAddOnGroupId: String = "",
    @get:PropertyName("petpooja_addon_group_id")
    @set:PropertyName("petpooja_addon_group_id")
    var petpoojaAddOnGroupId: String = "",
)
