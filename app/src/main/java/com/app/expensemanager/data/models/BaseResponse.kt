package com.app.expensemanager.data.models

import com.google.gson.annotations.SerializedName

data class BaseResponse(

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("error")
    val error: String? = null,

)
