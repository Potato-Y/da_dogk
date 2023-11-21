package com.github.da_dogk.interface_folder

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface groupStudyService {
    @GET("주소")
    fun myStudy(@Query("name") name: String) : Call<MyStudyService>
}