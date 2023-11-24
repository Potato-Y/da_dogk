package com.github.da_dogk.interface_folder

import com.github.da_dogk.response.LoginResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface LoginService {
    @FormUrlEncoded //body로 바꿔야함
    @POST("authenticate")
    fun login(
        @Field("email") email: String,
        @Field("password") pw: String
    ): Call<LoginResponse>  //Call은 retrofit 선택
}

//컨트롤 + /
//   json으로 통신하는방법, 파라미터롤 통신하는방법이 있음
//   파라미터는 - @FormUrlEncoded 추가
//            - (@Field(전달하는 값의 변수명 - 서버에 있음) 변수명 : 자료형, @Field(전달하는 값의 변수명 - 서버에 있음) 변수명 : 자료형)

