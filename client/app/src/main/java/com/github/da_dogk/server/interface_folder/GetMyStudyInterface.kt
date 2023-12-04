package com.github.da_dogk.server.interface_folder

import com.github.da_dogk.server.response.MyStudyResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface GetMyStudyInterface {
    @GET("study/subjects/{userId}")
    fun getCategories(@Header("Authorization") authorization: String): Call<List<MyStudyResponse>>
}
// 단일 객체 받을거면 Call<MyStudyResponse>로