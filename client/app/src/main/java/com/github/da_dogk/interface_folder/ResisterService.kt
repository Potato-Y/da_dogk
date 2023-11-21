package com.github.da_dogk.interface_folder

import com.github.da_dogk.response.LoginResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ResisterService {

    @FormUrlEncoded
    @POST("주소")
    fun register(
        @Field("email") email: String,
        @Field("password") pw: String,
        @Field("nickname") nickname: String
    ): Call<LoginResponse>  //Call은 retrofit 선택
}