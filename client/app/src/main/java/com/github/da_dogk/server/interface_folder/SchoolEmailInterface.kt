package com.github.da_dogk.server.interface_folder

import com.github.da_dogk.server.request.MyStudyRequest
import com.github.da_dogk.server.request.SchoolEmailRequest
import com.github.da_dogk.server.request.SchoolNumberRequest
import com.github.da_dogk.server.response.MySchoolResponse
import com.github.da_dogk.server.response.SchoolEmailResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface SchoolEmailInterface {
    @POST("school/auth/mail")
    fun sendEmail(
        @Header("Authorization") authorization: String,
        @Body request: SchoolEmailRequest
    ): Call<SchoolEmailResponse>

    @POST("school/auth/verify")
    fun sendNumber(
        @Header("Authorization") authorization: String,
        @Body request: SchoolNumberRequest
    ): Call<SchoolEmailResponse>

    @GET("school")
    fun mySchool(): Call<MySchoolResponse>
}