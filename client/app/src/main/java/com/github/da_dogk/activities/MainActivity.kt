package com.github.da_dogk.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.github.da_dogk.R
import com.github.da_dogk.interface_folder.LoginService
import com.github.da_dogk.response.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() { //LoginActivity

    lateinit var email : EditText
    lateinit var password : EditText
    lateinit var button : Button

    lateinit var btnResister : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        email = findViewById(R.id.editTextEmail_Login)
        password = findViewById(R.id.editTextPassword_Login)
        button = findViewById(R.id.button_login)

        btnResister = findViewById(R.id.registerTextButton)

        btnResister.setOnClickListener {
            Intent(this, ResisterActivity::class.java).run {
                startActivity(this)
            }
        }

        //레트로핏 설정
        val retrofit = Retrofit.Builder()
            .baseUrl("https://dadogk.duckdns.org/")    //"http://localhost:8080/"
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(LoginService::class.java)

        //버튼 클릭시 로그인
        button.setOnClickListener {
            val emailStr = email.text.toString()
            val pwStr = password.text.toString()
            service.login(emailStr,pwStr).enqueue(object : Callback<LoginResponse> {
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