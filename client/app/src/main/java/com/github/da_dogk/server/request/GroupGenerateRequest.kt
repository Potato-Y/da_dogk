package com.github.da_dogk.server.request

data class GroupGenerateRequest(
    val groupName: String,
    val groupIntro: String,
    val password: String?
)
