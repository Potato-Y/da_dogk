package com.github.da_dogk.server.response

import java.io.Serializable

data class User(
    val userId: Int,
    val email: String,
    val nickname: String,
    val todatStudyTime: Int
) : Serializable