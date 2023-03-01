package com.app.expensemanager.data.network

sealed class NetworkResultState<out T> {

    data class Success<out T>(val data: T) : NetworkResultState<T>()
    data class Error(val message: String) : NetworkResultState<Nothing>()
    object Loading : NetworkResultState<Nothing>()
}
