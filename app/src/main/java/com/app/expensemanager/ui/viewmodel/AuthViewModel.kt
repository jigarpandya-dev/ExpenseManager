package com.app.expensemanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.expensemanager.data.models.LoginRequest
import com.app.expensemanager.data.models.LoginResponse
import com.app.expensemanager.data.network.ExpenseRepository
import com.app.expensemanager.data.network.NetworkResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: ExpenseRepository): ViewModel() {

    private var _loginResult = MutableSharedFlow<NetworkResultState<LoginResponse>>()
    val loginResult: SharedFlow<NetworkResultState<LoginResponse>> = _loginResult

    fun login(login: LoginRequest){
        viewModelScope.launch {
            repository.login(login).collect{
                _loginResult.emit(it)
            }
        }
    }
}