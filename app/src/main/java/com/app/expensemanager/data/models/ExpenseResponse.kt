package com.app.expensemanager.data.models

import com.google.gson.annotations.SerializedName

data class ExpenseResponse(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("user")
	val user: String? = null,

	@field:SerializedName("transactionType")
	val transactionType: String? = null,

	@field:SerializedName("amount")
	val amount: Int? = null,

	@field:SerializedName("notes")
	val notes: String? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("category")
	val category: String? = null
)
