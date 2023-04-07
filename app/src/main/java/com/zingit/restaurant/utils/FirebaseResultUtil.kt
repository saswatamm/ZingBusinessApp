package com.zingit.restaurant.utils

data class FirebaseResultUtil<out T>(val status: Status, val data: T?, val error: String?) {

        enum class Status {
                SUCCESS,
                ERROR,
                LOADING
        }

        companion object {
                fun <T> success(data: T?): FirebaseResultUtil<T> {
                        return FirebaseResultUtil(Status.SUCCESS, data, null)
                }

                fun <T> error(error: String): FirebaseResultUtil<T> {
                        return FirebaseResultUtil(Status.ERROR, null, error)
                }

                fun <T> loading(): FirebaseResultUtil<T> {
                        return FirebaseResultUtil(Status.LOADING, null, null)
                }
        }

        override fun toString(): String {
                return "Result(status=$status, data=$data, error=$error)"
        }
}