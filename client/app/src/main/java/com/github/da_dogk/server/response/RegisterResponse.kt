package com.github.da_dogk.server.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("nickname")
    val nickname: String,
    val userId: Int,
    val timestamp: String,
    val status: Int,
    val trace: String,
    val message: String,
    val path: String
)
