package com.example.newsapp.network

import retrofit2.Response


abstract class ApiCallHandler {

    suspend fun <T> makeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                body?.let {
                    return NetworkResult.Success(body)
                } ?: kotlin.run {
                    return NetworkResult.SuccessWithNoResult("No data but api is success")
                }
            }
            return error(errorMessage = "${response.code()} ${response.message()}")
        } catch (e: Exception) {
            return error(errorMessage = e.message ?: e.toString())
        }
    }

    private fun <T> error(errorMessage: String): NetworkResult<T> =
        NetworkResult.Error("Api call failed $errorMessage")
}
