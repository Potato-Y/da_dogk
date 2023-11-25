package com.github.da_dogk.server.interface_folder

import com.github.da_dogk.server.request.LoginRequest
import com.github.da_dogk.server.response.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface LoginInterface {
    @POST("authenticate")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}

//전에 쓴 @field
//@FormUrlEncoded //body로 바꿔야함
//@POST("authenticate")
//fun login(
//    @Field("email") email: String,
//    @Field("password") pw: String
//): Call<LoginResponse>  //Call은 retrofit 선택