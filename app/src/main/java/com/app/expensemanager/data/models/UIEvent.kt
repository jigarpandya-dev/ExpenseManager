package com.app.expensemanager.data.models

sealed class UIEvent{
    data class Submit(val expense: Expense,val id:String?=null) : UIEvent()
    data class AddCategory(val category:String):UIEvent()
    object Idle:UIEvent()
}