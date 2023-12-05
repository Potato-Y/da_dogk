package com.github.da_dogk.activities.group

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.github.da_dogk.R
import com.github.da_dogk.server.interface_folder.GroupGenerateInterface
import com.github.da_dogk.server.request.GroupGenerateRequest
import com.github.da_dogk.server.response.GroupGenerateResponse
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GroupGenerateActivity : AppCompatActivity() {

    lateinit var groupName: EditText
    lateinit var groupIntro: EditText
    lateinit var groupPassword: EditText
    lateinit var groupGenerateButton: Button
    lateinit var goBack: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_generate)

        groupName = findViewById(R.id.ET_group_name)
        groupIntro = findViewById(R.id.ET_group_intro)
        groupPassword = findViewById(R.id.ET_group_password)
        goBack = findViewById(R.id.b_go_back)

        groupGenerateButton = findViewById(R.id.B_group_generate)

        goBack.setOnClickListener {
            onBackPressed()
        }

        //activity에서는 requireActivity()호출 할 필요 없음
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val jwtToken = sharedPreferences.getString("accessToken", "")

        val retrofit = Retrofit.Builder()
            .baseUrl("https://dadogk.duckdns.org/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient(jwtToken))
            .build()

        val service = retrofit.create(GroupGenerateInterface::class.java)

        groupGenerateButton.setOnClickListener {
            val name = groupName.text.toString()
            val intro = groupIntro.text.toString()
            val password = groupPassword.text.toString()

            //null을 넣어줘야함
            val request = if (password.isBlank()) {
                GroupGenerateRequest(name, intro, null)
            } else {
                GroupGenerateRequest(name, intro, password)
            }

            service.addGroup("Bearer $jwtToken", request)
                .enqueue(object : Callback<GroupGenerateResponse> {
                    override fun onResponse(
                        call: Call<GroupGenerateResponse>,
                        response: Response<GroupGenerateResponse>
                    ) {
                        if (response.isSuccessful) {
                            val result = response.body()
                            Log.d("그룹 생성", "${result}")
                            Toast.makeText(applicationContext, "그룹이 생성되었습니다", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Log.e("그룹 생성", "실패: ${response.code()}")
                            Toast.makeText(applicationContext, "그룹생성 error", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                    override fun onFailure(call: Call<GroupGenerateResponse>, t: Throwable) {
                        Log.e("그룹 생성", "${t.localizedMessage}")
                    }
                })
        }
    }

    private fun createOkHttpClient(jwtToken: String?): OkHttpClient {
        val httpClient = OkHttpClient.Builder()

        if (!jwtToken.isNullOrBlank()) {
            httpClient.addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer $jwtToken")
                    .method(original.method(), original.body())

                val request = requestBuilder.build()
                chain.proceed(request)
            }
        }

        return httpClient.build()
    }
}