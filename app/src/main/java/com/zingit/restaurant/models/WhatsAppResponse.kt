package com.zingit.restaurant.models

data class WhatsAppResponse(
    val body: Any,
    val headers: Any,
    val isBase64Encoded: Boolean,
    val message: String,
    val status: String,
    val statusCode: Int
)