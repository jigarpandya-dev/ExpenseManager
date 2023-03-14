package com.app.expensemanager.data.network


import com.app.expensemanager.data.models.Expense
import com.app.expensemanager.data.models.LoginRequest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ExpenseRepository @Inject constructor(private val expenseService: ExpenseService) : BaseApiResponse(){

     suspend fun getAllExpenses(user:String) = flow{
        //emit(NetworkResult.Loading())
        emit(safeApiCall {  expenseService.getAllExpenses(user)})
    }

    suspend fun addExpense(expense: Expense) = flow {
        emit(NetworkResultState.Loading)
        emit(safeApiCall { expenseService.addExpense(expense) })
    }

    suspend fun updateExpense(id:String, expense: Expense) = flow {
        emit(NetworkResultState.Loading)
        emit(safeApiCall { expenseService.updateExpense(id,expense) })
    }

    suspend fun deleteExpense(id: String) = flow {
        emit(safeApiCall { expenseService.deleteExpense(id) })
    }

    suspend fun login(login: LoginRequest) = flow {
        emit(NetworkResultState.Loading)
        emit(safeApiCall { expenseService.login(login) })
    }
}