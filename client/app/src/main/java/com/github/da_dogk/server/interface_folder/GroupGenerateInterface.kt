package com.github.da_dogk.server.interface_folder

import com.github.da_dogk.server.request.GroupGenerateRequest
import com.github.da_dogk.server.request.GroupPasswordRequest
import com.github.da_dogk.server.response.GroupAvgTimeResponse
import com.github.da_dogk.server.response.GroupGenerateResponse
import com.github.da_dogk.server.response.User
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

    //내가 가입한 그룹들
    @GET("groups")
    fun showMyGroup() : Call<List<GroupGenerateResponse>>

    //그룹가입 유저 가져오기
    @GET("groups/{groupId}/members")
    fun showMembers(
        @Path("groupId") groupId: String,
    ): Call<List<User>>

    @GET("groups/{groupId}/study/average")
    fun showAverageTime(
        @Path("groupId") groupId: String
    ): Call<GroupAvgTimeResponse>
}