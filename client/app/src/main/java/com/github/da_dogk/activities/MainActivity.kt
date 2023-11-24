package com.github.da_dogk.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.github.da_dogk.R
import com.github.da_dogk.activities.fragment.NaviActivity
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
    lateinit var textView: TextView
    lateinit var btnResister : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        email = findViewById(R.id.editTextEmail_Login)
        password = findViewById(R.id.editTextPassword_Login)
        button = findViewById(R.id.button_login)
        textView = findViewById(R.id.change_home)

        btnResister = findViewById(R.id.registerTextButton)

        //회원가입하러가기 버튼
        btnResister.setOnClickListener {
            Intent(this, ResisterActivity::class.java).run {
                startActivity(this)
            }
        }
        //로그인 텍스트 클릭시 홈 화면으로 이동 \
        textView.setOnClickListener {
            Intent(this, NaviActivity::class.java).run {
                startActivity(this)
            }
        }

        //레트로핏 설정
        val retrofit = Retrofit.Builder()
            .baseUrl("https://dadogk.duckdns.org/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(LoginService::class.java)

        //버튼 클릭시 로그인
        button.setOnClickListener {
            val emailStr = email.text.toString()
            val pwStr = password.text.toString()
            service.login(emailStr,pwStr).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        // 로그인 성공
                        val result = response.body()
                        Log.d("로그인", "${result}")
                        showToast("로그인 성공")
                    } else {
                        // 로그인 실패
                        Log.e("로그인", "실패: ${response.code()}")
                        showToast("로그인 실패")
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e("로그인","${t.localizedMessage}")
                }
            })
        }
    }
    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}