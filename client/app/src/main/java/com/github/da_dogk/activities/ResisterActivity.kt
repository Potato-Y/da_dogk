package com.github.da_dogk.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.github.da_dogk.R
import com.github.da_dogk.interface_folder.ResisterService
import com.github.da_dogk.response.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ResisterActivity : AppCompatActivity() {

    lateinit var id : EditText
    lateinit var password : EditText
    lateinit var name : EditText
    lateinit var email : EditText
    lateinit var phoneNum : EditText

    lateinit var button : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resister)

        id = findViewById(R.id.editTextId_Resister)
        password = findViewById(R.id.editTextPassword_Resister)
        name = findViewById(R.id.editTextName_Resister)
        email = findViewById(R.id.editTextEmail_Resister)
        phoneNum = findViewById(R.id.editTextPhoneNum_Resister)

        button = findViewById(R.id.button_Resister)


        //레트로핏 설정
        val retrofit = Retrofit.Builder()
            .baseUrl("https://3.34.141.115(서버 주소)")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ResisterService::class.java)

        button.setOnClickListener {
            val idStr = id.text.toString()
            val pwStr = password.text.toString()
            val nameStr = name.text.toString()
            val emailStr = email.text.toString()
            val phoneStr = phoneNum.text.toString()

            service.register(idStr,pwStr,nameStr,emailStr,phoneStr).enqueue(object :
                Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    val result = response.body()
                    Log.d("로그인","${result}")
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e("로그인","${t.localizedMessage}")
                }
            })
        }
    }
}