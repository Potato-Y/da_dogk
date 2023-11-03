package com.github.da_dogk.response

data class LoginResponse(
    val email: Int,
    val id: Int,
    val name: String,
    val password: String,
    val phoneNum: String,
    val seq: String,
    val success:Boolean
)
//성공시 success : true
//실패시 {
//          "success" : false
//      }
//이런식으로 넘김