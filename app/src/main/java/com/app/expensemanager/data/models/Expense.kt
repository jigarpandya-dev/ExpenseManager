package com.app.expensemanager.data.models

data class Expense(
    var date: String?=null,
    var user:String?=null,
    var title: String?=null,
    var category: String?=null,
    var amount:Double?=null,
    var notes:String?=null,
    var transactionType:String?=null
)
