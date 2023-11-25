package com.github.da_dogk.server.interface_folder

import com.github.da_dogk.server.response.MyStudyResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MyStudyService {
    @GET("주소")
    fun myStudy(@Query("category") category: String) :Call<MyStudyResponse>
}