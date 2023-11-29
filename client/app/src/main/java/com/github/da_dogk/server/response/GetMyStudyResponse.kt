package com.github.da_dogk.server.response

data class GetMyStudyResponse(
    val id: Int,
    val user: User,
    val title: String
)
