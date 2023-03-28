package com.zingit.restaurant.utils

import okio.IOException


class NoConnectivityException: IOException() {
    override val message: String
        get() = "No internet connection"
}