package com.github.da_dogk.interface_folder

import com.github.da_dogk.response.LoginResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SignService {

    /*
    json으로 통신하는방법, 파라미터롤 통신하는방법이 있음
    파라미터는 - @FormUrlEncoded 추가
             - (@Field(전달하는 값의 변수명 - 서버에 있음) 변수명 : 자료형, @Field(전달하는 값의 변수명 - 서버에 있음) 변수명 : 자료형)
     */

    @FormUrlEncoded
    @POST("login.php(자원 주소)")
    fun login(@Field("id") id:String, @Field("password") pw:String) : Call<LoginResponse>  //Call은 retrofit 선택

}

//3.34.141.115/login.php
//반환 형태가 json이면 클래스로 바꿔서
//button, @Post 부분 Body->loginResponse

//버튼 클릭시 retrofit통신, 그걸 하려면 인터페이스 만들어야함