package com.github.da_dogk.response

import com.google.gson.annotations.SerializedName

data class GroupGenerateResponse(
    @SerializedName("name")
    val name: String,

    @SerializedName("intro")
    val intro: String,

    @SerializedName("group_password")
    val group_password: String
)
