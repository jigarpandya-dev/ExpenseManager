package com.app.expensemanager.data.models

sealed class UIEvent{
    data class Submit(val expense: Expense,val id:String?=null) : UIEvent()
}