package com.github.da_dogk.server.request

data class RegisterRequest(
    val email: String,
    val password: String,
    val nickname: String
)