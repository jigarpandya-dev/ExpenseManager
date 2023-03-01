package com.app.expensemanager.data.network

import com.app.expensemanager.data.models.Expense
import com.app.expensemanager.data.models.ExpenseResponse
import com.app.expensemanager.data.models.LoginRequest
import com.app.expensemanager.data.models.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ExpenseService {

    @GET("api/getAll")
    suspend fun getAllExpenses(@Query("user") user:String):Response<List<ExpenseResponse>>

    @POST("api/post")
    suspend fun addExpense(@Body expense: Expense):Response<Expense>

    @POST("api/user/login")
    suspend fun login(@Body login: LoginRequest):Response<LoginResponse>

}