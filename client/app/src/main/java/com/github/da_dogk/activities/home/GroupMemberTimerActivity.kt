package com.github.da_dogk.activities.home

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.da_dogk.R
import com.github.da_dogk.adapter.GroupAdapter
import com.github.da_dogk.adapter.MemberAdapter
import com.github.da_dogk.server.RetrofitClient
import com.github.da_dogk.server.interface_folder.GroupGenerateInterface
import com.github.da_dogk.server.response.GroupGenerateResponse
import com.github.da_dogk.server.response.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GroupMemberTimerActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var memberAdapter: MemberAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_member_timer)

        //리사이클러뷰 설정
        recyclerView = findViewById(R.id.rv_members)
        recyclerView.layoutManager = LinearLayoutManager(this)
        memberAdapter = MemberAdapter()
        recyclerView.adapter = memberAdapter



        val groupId = intent.getStringExtra("id")

        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val jwtToken = sharedPreferences.getString("accessToken", "")

        val retrofit = RetrofitClient.createRetrofitInstance(jwtToken)

        val service = retrofit.create(GroupGenerateInterface::class.java)

        service.showMembers("$groupId").enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    val member = response.body()

                    if (member != null && member.isNotEmpty()) {
                        memberAdapter.setMember(member)
                        memberAdapter.notifyDataSetChanged()
                    }
                    Log.d("유저 불러오기", "성공 : $member")
                } else {
                    Log.e("유저 불러오기", "실패: ${response.code()}")
                    Toast.makeText(applicationContext, "유저 불러오기 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Log.e("유저 불러오기", "${t.localizedMessage}")
                Toast.makeText(applicationContext, "네트워크 오류", Toast.LENGTH_SHORT).show()
            }
        })
    }
}