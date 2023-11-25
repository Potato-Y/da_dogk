package com.github.da_dogk.server.request


data class LoginRequest(
    val email: String,
    val password: String
)