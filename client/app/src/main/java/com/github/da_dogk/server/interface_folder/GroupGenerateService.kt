package com.github.da_dogk.server.interface_folder

import com.github.da_dogk.server.response.GroupGenerateResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface GroupGenerateService {
    @FormUrlEncoded
    @POST("주소")
    fun generate(
        @Field("name") name: String,
        @Field("intro") intro: String,
        @Field("group_password") group_password: String
    ): Call<GroupGenerateResponse>
}