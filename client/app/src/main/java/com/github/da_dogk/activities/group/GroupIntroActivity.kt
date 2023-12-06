package com.github.da_dogk.activities.group

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import com.github.da_dogk.R
import com.github.da_dogk.server.RetrofitClient
import com.github.da_dogk.server.interface_folder.GroupGenerateInterface
import com.github.da_dogk.server.request.GroupPasswordRequest
import com.github.da_dogk.server.request.SchoolNumberRequest
import com.github.da_dogk.server.response.GroupGenerateResponse
import com.github.da_dogk.server.response.SchoolEmailResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GroupIntroActivity : AppCompatActivity() {
    lateinit var groupName: TextView
    lateinit var groupTime: TextView
    lateinit var groupMembers: TextView
    lateinit var groupSeeMembers: TextView
    lateinit var groupIntro: TextView
    lateinit var joinButton: Button
    lateinit var backButton: ImageButton

    lateinit var groupPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_intro)



        val groupId = intent.getStringExtra("id")
        showGroupDetails(groupId)


        //뒤로가기
        backButton = findViewById(R.id.b_go_back3)
        backButton.setOnClickListener {
            onBackPressed()
        }

    }

    private fun showGroupDetails(groupId: String?) {
        if (groupId != null) {
            val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val jwtToken = sharedPreferences.getString("accessToken", "")

            val retrofit = RetrofitClient.createRetrofitInstance(jwtToken)
            val service = retrofit.create(GroupGenerateInterface::class.java)

            service.getGroupDetails("$groupId").enqueue(object : Callback<GroupGenerateResponse> {
                override fun onResponse(call: Call<GroupGenerateResponse>, response: Response<GroupGenerateResponse>) {
                    if (response.isSuccessful) {
                        val group = response.body()

                        groupName = findViewById(R.id.tv_group_name2)
                        groupTime = findViewById(R.id.tv_avg_time)
                        groupMembers = findViewById(R.id.tv_members)
                        groupSeeMembers = findViewById(R.id.tv_see_members)
                        groupIntro = findViewById(R.id.tv_group_intro)
                        joinButton = findViewById(R.id.b_join)

                        // 가져온 그룹 정보를 TextView에 설정
                        groupName.text = group?.groupName
                        groupMembers.text = group?.memberNumber.toString()
                        groupIntro.text = group?.groupIntro

                        val password = group?.privacyState

                        groupMembers.setOnClickListener {

                        }

                        //그룹 가입하기 버튼
                        joinButton.setOnClickListener {
                            //비밀번호 있을때
                            if(password == true) {
                                val builder = AlertDialog.Builder(this@GroupIntroActivity)
                                val inflater = layoutInflater
                                val dialogView = inflater.inflate(R.layout.dialog1, null)

                                builder.setView(dialogView)
                                builder.setTitle("그룹 비밀번호 입력")
                                groupPassword = dialogView.findViewById(R.id.ET_input_title)
                                val password = groupPassword.text.toString()

                                builder.setPositiveButton("확인") { _, _ ->
                                    if(password.isNotEmpty()){
                                        val request = GroupPasswordRequest(password)
                                        service.joinGroupTrue("$groupId",request).enqueue(object : Callback<GroupGenerateResponse>{
                                            override fun onResponse(call: Call<GroupGenerateResponse>, response: Response<GroupGenerateResponse>) {
                                                if (response.isSuccessful) {
                                                    val result = response.body()
                                                    Log.d("그룹 패스워드 입력", "${result}")
                                                    Toast.makeText(applicationContext, "가입완료", Toast.LENGTH_SHORT).show()

                                                } else {
                                                    Log.e("그룹 패스워드 입력", "실패: ${response.code()}")
                                                    Toast.makeText(applicationContext, "암호가 틀렸습니다.", Toast.LENGTH_SHORT).show()
                                                }
                                            }

                                            override fun onFailure(call: Call<GroupGenerateResponse>, t: Throwable) {
                                                Log.e("그룹 패스워드 입력", "${t.localizedMessage}")
                                            }
                                        })
                                    } else {
                                        // 비밀번호를 입력하지 않았을 때의 처리
                                        Toast.makeText(this@GroupIntroActivity, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                // "Cancel" 버튼 추가
                                builder.setNegativeButton("취소") { dialog, _ ->
                                    Toast.makeText(applicationContext, "취소했습니다.", Toast.LENGTH_SHORT).show()
                                    dialog.dismiss()
                                }

                                // 다이얼로그 생성 및 표시
                                val alertDialog = builder.create()
                                alertDialog.show()

                                //비밀번호 없을때
                            } else{
                                service.joinGroupTrue("$groupId",GroupPasswordRequest("")).enqueue(object : Callback<GroupGenerateResponse>{
                                    override fun onResponse(call: Call<GroupGenerateResponse>, response: Response<GroupGenerateResponse>) {
                                        if (response.isSuccessful) {
                                            val result = response.body()
                                            Log.d("그룹 가입", "${result}")
                                            Toast.makeText(applicationContext, "그룹 가입 성공", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Log.e("그룹 가입", "실패: ${response.code()}")
                                            Toast.makeText(applicationContext, "그룹 가입 실패", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    override fun onFailure(call: Call<GroupGenerateResponse>, t: Throwable) {
                                        Log.e("그룹 가입", "${t.localizedMessage}")
                                    }
                                })
                            }
                        }

                    } else {

                    }
                }

                override fun onFailure(call: Call<GroupGenerateResponse>, t: Throwable) {
                    // 통신 실패 시 처리
                }
            })
        }
    }
}