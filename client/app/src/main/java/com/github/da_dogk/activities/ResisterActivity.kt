package com.github.da_dogk.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.github.da_dogk.NaviActivity
import com.github.da_dogk.R
import com.github.da_dogk.interface_folder.ResisterService
import com.github.da_dogk.response.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/*
import com.github.da_dogk.interface_folder.ResisterService
import com.github.da_dogk.response.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
*/
class ResisterActivity : AppCompatActivity() {

    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var nickname: EditText



    lateinit var button: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resister)

        email = findViewById(R.id.editTextEmail_Resister)
        password = findViewById(R.id.editTextPassword_Resister)
        nickname = findViewById(R.id.editTextNickname_Resister)

        button = findViewById(R.id.button_Resister)


        button.setOnClickListener {
            Intent(this, NaviActivity::class.java).run {
                startActivity(this)
            }
        }
/*
        //레트로핏 설정
        val retrofit = Retrofit.Builder()
            .baseUrl("주소")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ResisterService::class.java)

        button.setOnClickListener {
            val pwStr = password.text.toString()
            val nameStr = nickname.text.toString()
            val emailStr = email.text.toString()


            service.register(emailStr, pwStr, nameStr).enqueue(object :
                Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    val result = response.body()
                    Log.d("로그인", "${result}")
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e("로그인", "${t.localizedMessage}")
                }
            })
        }

 */

    }
}