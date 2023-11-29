// accessToken넘기기


package com.github.da_dogk.activities.login

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.github.da_dogk.R
import com.github.da_dogk.activities.fragment.NaviActivity
import com.github.da_dogk.server.RetrofitClient
import com.github.da_dogk.server.interface_folder.LoginInterface
import com.github.da_dogk.server.request.LoginRequest
import com.github.da_dogk.server.response.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() { //LoginActivity

    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var button: Button
    lateinit var textView: TextView
    lateinit var btnResister: TextView

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
        //로그인 텍스트 클릭시 홈 화면으로 이동
        textView.setOnClickListener {
            Intent(this, NaviActivity::class.java).run {
                startActivity(this)
            }
        }

        // 이미 로그인된 사용자라면 NaviActivity로 이동 (굳이 지금 쓸 필요는 없다)
//        if (isUserLoggedIn()) {
//            Intent(this, NaviActivity::class.java).run {
//                startActivity(this)
//                finish() // 현재 액티비티를 종료하여 뒤로 가기 시 로그인 화면으로 돌아가지 않도록 함
//            }
//        }

        //레트로핏 설정
        val service = RetrofitClient.createService(LoginInterface::class.java)

        //버튼 클릭시 로그인
        button.setOnClickListener {
            val emailStr = email.text.toString()
            val pwStr = password.text.toString()
            val request = LoginRequest(emailStr, pwStr)

            service.login(request).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val result = response.body()

                        if (result != null) {
                            val accessToken = result.accessToken

                            if (accessToken.isNullOrBlank()) {
                                Log.e("로그인", "AccessToken이 null이거나 blank입니다.")
                            } else {
                                // Save the token, change token to accessToken
                                saveToken(accessToken)
                                Log.d("로그인", "AccessToken: $accessToken")
                                showToast("로그인 성공")

//                                val user = result.user

                                // 토큰과 함께 NaviActivity로 이동
                                Intent(this@MainActivity, NaviActivity::class.java).apply {
                                    putExtra(NaviActivity.EXTRA_ACCESS_TOKEN, accessToken)
//                                    putExtra(NaviActivity.EXTRA_USER, user)
                                    startActivity(this)
                                    finish() //해야하나?
                                }
                            }
                        } else {
                            Log.e("로그인", "Response body is null")
                        }
                    } else {
// 로그인 실패
                        Log.e("로그인", "실패: ${response.code()}")
                        showToast("로그인 실패")
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e("로그인", "${t.localizedMessage}")
                }
            })
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    //로그인 정보 저장
    private fun isUserLoggedIn(): Boolean {
        val sharedPreferences: SharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val jwtToken: String? = sharedPreferences.getString("accessToken", "")
        return !jwtToken.isNullOrBlank()
    }

    private fun saveToken(token: String?) {
        if (!token.isNullOrBlank()) {
            val sharedPreferences: SharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
            sharedPreferences.edit().putString("accessToken", token).apply()
        }
    }
}