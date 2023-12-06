package com.github.da_dogk.server.interface_folder

import com.github.da_dogk.server.request.GroupGenerateRequest
import com.github.da_dogk.server.request.GroupPasswordRequest
import com.github.da_dogk.server.response.GroupGenerateResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface GroupGenerateInterface {

    //그룹 만들기
    @POST("groups")
    fun addGroup(
        @Body request: GroupGenerateRequest
    ): Call<GroupGenerateResponse>

    //전체 그룹 조회 api
    @GET("groups?groupName=")
    fun showGroup(): Call<List<GroupGenerateResponse>>

    //그룹 상세페이지
    @GET("groups/{groupId}")
    fun getGroupDetails(@Path("groupId") groupId: String): Call<GroupGenerateResponse>

    //그룹 비번 있을때
    @POST("groups/{groupId}/members")
    fun joinGroupTrue(
        @Path("groupId") groupId: String,
        @Body request: GroupPasswordRequest
    ): Call<GroupGenerateResponse>

    //그룹 비번 없을때
    @POST("groups/{groupId}/members")
    fun joinGroupFalse(
        @Path("groupId") groupId: String
    ): Call<GroupGenerateResponse>
}