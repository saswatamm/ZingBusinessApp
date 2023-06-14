package com.zingit.restaurant.models.order

import com.google.firebase.firestore.PropertyName

data class OrderDetailsModel(
    @get:PropertyName("advanced_order")
    @set:PropertyName("advanced_order")
    var advancedOrder: String = "",
    @get:PropertyName("created_on")
    @set:PropertyName("created_on")
    var createdOn: String = "",
    @get:PropertyName("dc_tax_amount")
    @set:PropertyName("dc_tax_amount")
    var dcTaxAmount: String = "",
    @get:PropertyName("delivery_charges")
    @set:PropertyName("delivery_charges")
    var deliveryCharges: String = "",
    @get:PropertyName("description")
    @set:PropertyName("description")
    var decsription: String = "",
    @get:PropertyName("discount_total")
    @set:PropertyName("discount_total")
    var discountTotal: String = "",
    @get:PropertyName("discount_type")
    @set:PropertyName("discount_type")
    var discountType: String = "",
    @get:PropertyName("enable_delivery")
    @set:PropertyName("enable_delivery")
    var enableDelivery: Int=0,
    @get:PropertyName("min_prep_time")
    @set:PropertyName("min_prep_time")
    var minPrepTime: String = "",
    @get:PropertyName("orderID")
    @set:PropertyName("orderID")
    var orderId: String = "",
    @get:PropertyName("order_type")
    @set:PropertyName("order_type")
    var orderType: String = "",
    @get:PropertyName("packing_charges")
    @set:PropertyName("packing_charges")
    var packingCharges: String = "",
    @get:PropertyName("payment_type")
    @set:PropertyName("payment_type")
    var paymentType: String = "",
    @get:PropertyName("dc_gst_details")
    @set:PropertyName("dc_gst_details")
    var dcGstDetails: ArrayList<GstModel> = arrayListOf(),
    @get:PropertyName("pc_gst_details")
    @set:PropertyName("pc_gst_details")
    var pcGstDetails: ArrayList<GstModel> = arrayListOf(),
    @get:PropertyName("pc_tax_amount")
    @set:PropertyName("pc_tax_amount")
    var pcTaxAmount: String = "",
    @get:PropertyName("preorder_date")
    @set:PropertyName("preorder_date")
    var preorderDate: String = "",
    @get:PropertyName("preorder_time")
    @set:PropertyName("preorder_time")
    var preorderTime: String = "",
    @get:PropertyName("sc_tax_amount")
    @set:PropertyName("sc_tax_amount")
    var scTaxAmount: String = "",
    @get:PropertyName("service_charge")
    @set:PropertyName("service_charge")
    var serviceCharge: String = "",
    @get:PropertyName("table_no")
    @set:PropertyName("table_no")
    var tableNo: String = "",
    @get:PropertyName("tax_total")
    @set:PropertyName("tax_total")
    var taxTotal: String = "",
    @get:PropertyName("total")
    @set:PropertyName("total")
    var total: String = "",
    )
    //Gst model has been used as common model for pcgstdetails and dcgstdetails
data class GstModel(
    @get:PropertyName("amount")
    @set:PropertyName("amount")
    var amount: String = "",
    @get:PropertyName("gst_liable")
    @set:PropertyName("gst_liable")
    var gstLiable: String = "",
)
