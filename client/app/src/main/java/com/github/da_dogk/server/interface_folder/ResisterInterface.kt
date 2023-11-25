package com.github.da_dogk.server.interface_folder

import com.github.da_dogk.server.request.RegisterRequest
import com.github.da_dogk.server.response.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ResisterInterface {
    @POST("signup")
    fun register(@Body request: RegisterRequest): Call<LoginResponse>
}