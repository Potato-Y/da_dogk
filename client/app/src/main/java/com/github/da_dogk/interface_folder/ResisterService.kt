package com.github.da_dogk.interface_folder

import com.github.da_dogk.response.LoginResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ResisterService{

    @FormUrlEncoded
    @POST("register.php(자원 주소)")
    fun register(@Field("id") id:String,
                 @Field("password") pw:String,
                 @Field("name") name:String,
                 @Field("phoneNum") phoneNum:String,
                 @Field("email") email:String ) : Call<LoginResponse>  //Call은 retrofit 선택

}