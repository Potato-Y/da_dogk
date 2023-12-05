package com.github.da_dogk.server.interface_folder

import com.github.da_dogk.server.request.MyStudyRequest
import com.github.da_dogk.server.response.MyStudyResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface MyStudyInterface {
    @POST("study/subjects")
    fun addCategory(
        @Header("Authorization") authorization: String,
        @Body request: MyStudyRequest
    ): Call<MyStudyResponse>

    @GET("study/subjects/1")
    fun showCategories(@Header("Authorization") authorization: String): Call<List<MyStudyResponse>>
}