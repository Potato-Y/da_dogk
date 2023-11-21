package com.github.da_dogk.response

import com.google.gson.annotations.SerializedName

data class MyStudyResponse(
    @SerializedName("category")
    val category: String
)