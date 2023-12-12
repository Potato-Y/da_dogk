package com.github.da_dogk.server.response

import com.google.gson.annotations.SerializedName

data class MyStudyResponse(
    val id: Int,
    val user: User,
    val title: String,
    val todayStudyTime: Int
)