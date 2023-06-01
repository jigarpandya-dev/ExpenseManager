package com.app.expensemanager.data.network

import com.app.expensemanager.data.models.*
import com.app.expensemanager.ui.expense.NewCategory
import retrofit2.Response
import retrofit2.http.*

interface ExpenseService {

    @POST("api/getAllExpenses")
    suspend fun getAllExpenses(@Body request: GetAllExpenseRequest): Response<List<ExpenseResponse>>

    @POST("api/addExpense")
    suspend fun addExpense(@Body expense: Expense): Response<Expense>

    @PATCH("api/updateExpense")
    suspend fun updateExpense(@Query("id") id: String, @Body expense: Expense): Response<Expense>

    @DELETE("api/deleteExpense")
    suspend fun deleteExpense(@Query("id") id: String): Response<BaseResponse>

    @POST("api/addCategory")
    suspend fun addCategory(@Body category: NewCategory): Response<BaseResponse>

    @GET("api/getCategories")
    suspend fun getCategories(@Query("user") user: String): Response<List<NewCategory>>

    @POST("api/user/login")
    suspend fun login(@Body login: LoginRequest): Response<LoginResponse>

}