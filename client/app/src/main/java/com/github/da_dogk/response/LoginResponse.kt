package com.github.da_dogk.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("nickname")
    val nickname: String
)


//성공시 success : true
//실패시 {
//          "success" : false
//      }
//이런식으로 넘김