package com.github.da_dogk.server.interface_folder

import com.github.da_dogk.server.response.GroupGenerateResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface GetGroupGenerateInterface {
    @GET("groups")
    fun getGroup(@Header("Authorization") authorization: String): Call<List<GroupGenerateResponse>>

}