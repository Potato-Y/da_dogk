package com.github.da_dogk.server.interface_folder

import com.github.da_dogk.server.response.GroupStudyResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface groupStudyService {
    @GET("주소")
    fun groupStudy(@Query("name") name: String) : Call<GroupStudyResponse>
}