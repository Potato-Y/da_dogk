package com.github.da_dogk.server.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val status: Int,
    val user: User
)

data class User(
    val userId: Int,
    val email: String,
    val nickname: String
) : Serializable