package com.github.da_dogk.server.response

data class GroupGenerateResponse(
    val id: Int,
    val groupName: String,
    val hostUser: User,
    val state: String,
    val groupType: String,
    val privacyState: Boolean,
    val createAt: String,
    val memberNumber: Int
)
