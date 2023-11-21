package com.github.da_dogk.response

import com.google.gson.annotations.SerializedName

data class GroupStudyResponse(
    @SerializedName("name")
    val name: String
)