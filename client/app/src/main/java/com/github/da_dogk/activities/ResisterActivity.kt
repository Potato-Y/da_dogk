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
import com.github.da_dogk.server.interface_folder.ResisterInterface
import com.github.da_dogk.server.request.RegisterRequest
import com.github.da_dogk.server.response.LoginResponse
import com.github.da_dogk.server.response.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
class ResisterActivity : AppCompatActivity() {

    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var nickname: EditText
    lateinit var go_login: TextView


    lateinit var button: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resister)

        email = findViewById(R.id.editTextEmail_Resister)
        password = findViewById(R.id.editTextPassword_Resister)
        nickname = findViewById(R.id.editTextNickname_Resister)
        go_login = findViewById(R.id.TV_go_login)

        button = findViewById(R.id.button_Resister)

        go_login.setOnClickListener{
            Intent(this, MainActivity::class.java).run {
                startActivity(this)
            }
        }


        //레트로핏 설정
        val retrofit = Retrofit.Builder()
            .baseUrl("https://dadogk.duckdns.org/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ResisterInterface::class.java)

        //버튼 클릭시 회원가입
        button.setOnClickListener {
            val pwStr = password.text.toString()
            val nameStr = nickname.text.toString()
            val emailStr = email.text.toString()

            val request = RegisterRequest(emailStr,pwStr,nameStr)

            service.register(request).enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    if (response.isSuccessful) {
                        // 회원가입 성공
                        val result = response.body()
                        Log.d("회원가입", "${result}")
                        showToast("회원가입 성공")
                    } else {
                        // 회원가입 실패
                        Log.e("회원가입", "실패: ${response.code()}")
                        showToast("회원가입 실패")
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    Log.e("회원가입", "${t.localizedMessage}")
                }
            })
        }

    }
    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}