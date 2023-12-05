package com.github.da_dogk.server.interface_folder

import com.github.da_dogk.server.request.GroupGenerateRequest
import com.github.da_dogk.server.response.GroupGenerateResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface GroupGenerateInterface {
    @POST("groups")
    fun addGroup(
        @Header("Authorization") authorization: String,
        @Body request: GroupGenerateRequest
    ): Call<GroupGenerateResponse>

    @GET("groups")
    fun showGroup(@Header("Authorization") authorization: String): Call<List<GroupGenerateResponse>>

}