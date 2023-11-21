package com.github.da_dogk.interface_folder

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MyStudyService {
    @GET("주소")
    fun myStudy(@Query("category") category: String) :Call<MyStudyService>
}