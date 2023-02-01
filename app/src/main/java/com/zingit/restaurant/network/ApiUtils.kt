package com.zingit.restaurant.network

import android.util.Log
import com.unorg.unorg.models.ApiResult
import com.unorg.unorg.models.ErrorRes
import kotlinx.coroutines.CancellationException
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiUtils @Inject constructor(private val retrofit: Retrofit) {

    private fun <T> parseError(response: Response<T>): ErrorRes.ApiError? {
        val converter: Converter<ResponseBody, ErrorRes.ApiError> = retrofit
            .responseBodyConverter(ErrorRes.ApiError::class.java, arrayOfNulls<Annotation>(0))
        Log.d("check", "parseError: check" + response.code())
        return try {
            converter.convert(response.errorBody()!!)
        }catch (ex: Exception) {
            null
        }
    }

    suspend fun <T> getResponse(request: suspend () -> Response<T>,
                                defaultErrorMessage: String
    ): ApiResult<T> {
        try {
            val result = request.invoke()
            if (result.isSuccessful) {
                return ApiResult.success(result.body())
            } else {
                val errorResponse = parseError(result)
                if(errorResponse is ErrorRes.ApiError)
                    return ApiResult.error(errorResponse.message?:"Something went wrong", errorResponse)
            }

        } catch (e: Throwable) {
            if(e is IOException)
                return ApiResult.error("Please Check Your Network Connection", null)
            else if (e is CancellationException)
                Log.d("Api Test", "Coroutine Cancelled")
            Log.e("Api error",e.stackTraceToString())
        }
        return ApiResult.error(defaultErrorMessage, null)
    }
}