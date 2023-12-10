package com.github.da_dogk.activities.home

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
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
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class GroupMemberTimerActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var memberAdapter: MemberAdapter
    lateinit var goback: ImageButton


    //웹소켓
    lateinit var myWebSocketListener: MyWebSocketListener
    private var isWebSocketConnected = false

    private lateinit var timerHandler: Handler
    private lateinit var timerRunnable: Runnable


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_member_timer)

        goback = findViewById(R.id.b_go_back)


        //리사이클러뷰 설정
        recyclerView = findViewById(R.id.rv_members)
        recyclerView.layoutManager = LinearLayoutManager(this)
        memberAdapter = MemberAdapter()
        recyclerView.adapter = memberAdapter

        goback.setOnClickListener {
            onBackPressed()
        }

        val groupId = intent.getStringExtra("id")

        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val jwtToken = sharedPreferences.getString("accessToken", "")

        val retrofit = RetrofitClient.createRetrofitInstance(jwtToken)

        val service = retrofit.create(GroupGenerateInterface::class.java)

        timerHandler = Handler(Looper.getMainLooper()) // timerHandler를 초기화


        timerRunnable = object : Runnable {
            override fun run() {
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

                timerHandler.postDelayed(this, 1000)
            }
        }
        timerHandler.postDelayed(timerRunnable, 1000)


        connectWebSocket(jwtToken!!)

    }


    private fun connectWebSocket(jwtToken: String) {
        myWebSocketListener = MyWebSocketListener(jwtToken)
        myWebSocketListener.startWebSocket()
    }

    inner class MyWebSocketListener(val accessToken : String): WebSocketListener() {

        private lateinit var webSocket: WebSocket

        fun startWebSocket() {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(HomeFragment.SW_SERVER_URL)
                .addHeader("Authorization", "Bearer $accessToken") // 헤더 추가
                .build()

            webSocket = client.newWebSocket(request, this)
            // 추가된 부분: 웹소켓 연결이 성공했을 때 true로 설정
            isWebSocketConnected = true
        }

        override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
            // WebSocket이 열릴 때 호출
            // 연결이 성공했을 때 초기화 작업 수행
            Log.d("WebSocket", "WebSocket이 열렸습니다.")
            // 연결이 열리면 STUDY_START 메시지 전송
            val getGroupInfoMessage = JSONObject().apply {
                put("type", "GET_GROUP_INFO")
            }

            sendMessage(getGroupInfoMessage.toString())
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            // 서버로부터 텍스트 메시지를 수신했을 때 호출
            // 메시지를 파싱하고 UI를 업데이트하는 작업 수행
            Log.d("WebSocket", "서버에서 메시지 수신: $text")
            // 추가된 부분: 메시지 파싱 및 처리
            try {
                val jsonResponse = JSONObject(text)
                val result = jsonResponse.getString("result")

                // 추가된 부분: GET_GROUP_INFO 응답 처리
                if (result == "CONNECTING_GROUP_MEMBER") {
                    val groupMemberResponses = jsonResponse.getJSONArray("groupMemberResponses")

                    // 추가 및 수정된 부분: 1번 그룹에 대한 정보만 처리
                    for (i in 0 until groupMemberResponses.length()) {
                        val groupMemberResponse = groupMemberResponses.getJSONObject(i)
                        val groupId = groupMemberResponse.getInt("groupId")

                        // 추가된 부분: 1번 그룹에 대한 정보만 처리
                        if (groupId == 1) {
                            val usersJsonArray = groupMemberResponse.getJSONArray("user")
                            val users = mutableListOf<User>()

                            for (j in 0 until usersJsonArray.length()) {
                                val userJson = usersJsonArray.getJSONObject(j)
                                val userId = userJson.getInt("userId")
                                val email = userJson.getString("email")
                                val nickname = userJson.getString("nickname")
                                val todayStudyTime = userJson.getInt("todayStudyTime")

                                val user = User(userId, email, nickname, todayStudyTime)
                                users.add(user)
                            }

                            // 추가된 부분: RecyclerView 어댑터에 데이터 설정 및 갱신

                                val adapter = MemberAdapter()
                                recyclerView.adapter = adapter
                                adapter.notifyDataSetChanged()


                            // 1번 그룹에 대한 정보를 처리했으므로 반복문 종료
                            break
                        }
                    }
                } else {
                    // 기존 코드와 동일한 메시지 처리 부분
                    // ...
                }
            } catch (e: JSONException) {
                Log.e("WebSocket", "메시지 파싱 실패: ${e.message}")
            }
        }


        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            // 서버로부터 바이트 스트림을 수신했을 때 호출
            // 바이트 데이터를 처리하는 작업 수행
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            // WebSocket이 닫히려고 할 때 호출
            // 연결 종료 전에 정리 작업 수행
            Log.d("WebSocket", "WebSocket이 닫히려 합니다. 코드: $code, 이유: $reason")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
            // WebSocket 연결이 실패했을 때 호출
            // 에러 핸들링 및 재연결 로직 수행
            Log.e("WebSocket", "WebSocket 연결 실패: ${t.localizedMessage}")
        }
        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
            // WebSocket 닫힘 처리
            Log.d("WebSocket", "WebSocket이 닫혔습니다: $code, 이유: $reason")
            // 필요시 여기에서 재연결을 처리할 수 있습니다.
        }

        fun sendMessage(message: String) {
            // WebSocket을 통해 메시지를 서버로 전송
            webSocket.send(message)
        }

        fun closeWebSocket() {
            // WebSocket 연결을 닫음
            webSocket.close(1000, "Goodbye, WebSocket!")
        }

    }
}