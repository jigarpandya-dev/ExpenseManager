package com.app.expensemanager.data.network

import org.json.JSONObject
import retrofit2.Response

abstract class BaseApiResponse {
    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResultState<T> {
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                body?.let {
                    return NetworkResultState.Success(body)
                }
            }
            val json = JSONObject(response.errorBody()?.string()?:"{message:Something went wrong}")
            val message = json.getString("message")
            return error("${response.code()} $message")
        } catch (e: Exception) {
            return error(e.message ?: e.toString())
        }
    }
    private fun <T> error(errorMessage: String): NetworkResultState<T> =
        NetworkResultState.Error(errorMessage)
}