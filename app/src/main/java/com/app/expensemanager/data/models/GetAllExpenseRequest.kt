package com.app.expensemanager.data.models

data class GetAllExpenseRequest(
    val user:String,
    val month:Int,
    val year:Int
)
