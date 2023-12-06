package com.github.da_dogk.server.interface_folder

import com.github.da_dogk.server.response.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface MyInfoInterface {
    @GET("user")
    fun showMyInfo(@Header("Authorization") authorization: String): Call<User>
}