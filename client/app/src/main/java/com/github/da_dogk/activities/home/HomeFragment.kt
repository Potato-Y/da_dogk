package com.github.da_dogk.activities.home

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.da_dogk.R
import com.github.da_dogk.adapter.MyGroupAdapter
import com.github.da_dogk.adapter.StudyAdapter
import com.github.da_dogk.server.RetrofitClient
import com.github.da_dogk.server.interface_folder.GroupGenerateInterface
import com.github.da_dogk.server.interface_folder.MyInfoInterface
import com.github.da_dogk.server.interface_folder.MyStudyInterface
import com.github.da_dogk.server.request.MyStudyRequest
import com.github.da_dogk.server.response.GroupGenerateResponse
import com.github.da_dogk.server.response.MyStudyResponse
import com.github.da_dogk.server.response.User
import com.google.android.material.tabs.TabLayout
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment() {

    lateinit var tabLayout: TabLayout
    lateinit var layoutMyStudy: FrameLayout
    lateinit var layoutGroupStudy: FrameLayout
    lateinit var buttonAddCategory: ImageButton
    lateinit var editTextInputTitle: EditText
    lateinit var timer: TextView
    lateinit var date: TextView
    lateinit var todayStudyTime: TextView

    lateinit var recyclerView : RecyclerView
    lateinit var studyAdapter: StudyAdapter

    lateinit var recyclerViewGroup : RecyclerView
    lateinit var myGroupAdapter: MyGroupAdapter

    //웹소켓
    lateinit var myWebSocketListener: MyWebSocketListener
    private var isWebSocketConnected = false

    private var timerRunning = false
    private var seconds = 0

    private lateinit var timerHandler: Handler
    private lateinit var timerRunnable: Runnable


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        tabLayout = view.findViewById(R.id.tab_layout_home)
        layoutMyStudy = view.findViewById(R.id.FL_myStudy)
        layoutGroupStudy = view.findViewById(R.id.FL_groupStudy)
        buttonAddCategory = view.findViewById(R.id.B_add_category)
        date = view.findViewById(R.id.TV_currentDate)
        timer = view.findViewById(R.id.TV_timer)
        todayStudyTime = view.findViewById(R.id.tv_today_study_time)

        //내공부 리사이클러뷰 설정
        recyclerView = view.findViewById(R.id.rv_my)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        studyAdapter = StudyAdapter()
        recyclerView.adapter = studyAdapter

        //내 그룹 리사이클러뷰 설정
        recyclerViewGroup = view.findViewById(R.id.rv_group)
        recyclerViewGroup.layoutManager = LinearLayoutManager(requireContext())
        myGroupAdapter = MyGroupAdapter()
        recyclerViewGroup.adapter = myGroupAdapter

        //현재 날짜 표시
        date.text = getCurrentFormattedDate()


        val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val jwtToken = sharedPreferences.getString("accessToken", "")

        //레트로핏 설정
        val retrofit = RetrofitClient.createRetrofitInstance(jwtToken)

        val service = retrofit.create(MyStudyInterface::class.java)
        val serviceMyInfo = retrofit.create(MyInfoInterface::class.java)
        val serviceMyGroup = retrofit.create(GroupGenerateInterface::class.java)


        //그룹 클릭시 멤버로 이동
        myGroupAdapter.setOnGroupClickListener(object : MyGroupAdapter.OnGroupClickListener {
            override fun onGroupClick(group: GroupGenerateResponse) {
                // 그룹 클릭 시 상세 페이지로 이동하는 코드
                navigateToGroupDetail(group.id.toString()) // 그룹의 고유 ID를 전달하거나 필요한 정보를 전달
            }
        })

            //내 카테고리 표시하기
        serviceMyInfo.showMyInfo("Bearer $jwtToken").enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                val user = response.body()
                val userId = user?.userId
                val studyTime = convertSecondsToFormattedTime(user!!.todayStudyTime)
                todayStudyTime.text = studyTime

                service.showCategories("$userId").enqueue(object : Callback<List<MyStudyResponse>> {
                    override fun onResponse(call: Call<List<MyStudyResponse>>, response: Response<List<MyStudyResponse>>) {
                        if (response.isSuccessful) {
                            val categories = response.body()

                            if (categories != null && categories.isNotEmpty()) {
                                studyAdapter.setStudy(categories)
                                studyAdapter.notifyDataSetChanged() // RecyclerView 갱신
                            }


                            Log.d("카테고리 불러오기", "성공 : $categories")
                        } else {
                            Log.e("카테고리 불러오기", "실패: ${response.code()}")
                            Toast.makeText(requireContext(), "카테고리 불러오기 실패", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<List<MyStudyResponse>>, t: Throwable) {
                        Log.e("카테고리 불러오기", "${t.localizedMessage}")
                        Toast.makeText(requireContext(), "네트워크 오류", Toast.LENGTH_SHORT).show()
                    }
                })

            }

            override fun onFailure(call: Call<User>, t: Throwable) {

            }
        })

        //카테고리 생성 버튼
        buttonAddCategory.setOnClickListener {
            // LayoutInflater를 사용하여 dialog.xml을 inflate
            val inflater = LayoutInflater.from(requireContext())
            val dialogView = inflater.inflate(R.layout.dialog1, null)
            editTextInputTitle = dialogView.findViewById(R.id.ET_input_title)

            // AlertDialog.Builder를 사용하여 다이얼로그 생성
            val builder = AlertDialog.Builder(requireContext())
            builder.setView(dialogView)
            builder.setTitle("카테고리 이름 입력")

            builder.setPositiveButton("확인") { _, _ ->
                val edit = editTextInputTitle.text.toString()
                val request = MyStudyRequest(edit)

                if(edit.isNotEmpty()){
                    service.addCategory("Bearer $jwtToken",request).enqueue(object : Callback<MyStudyResponse>{
                        override fun onResponse(call: Call<MyStudyResponse>, response: Response<MyStudyResponse>) {
                            if (response.isSuccessful) {
                                val result = response.body()
                                Log.d("카테고리 만들기", "${result}")
                                Toast.makeText(requireContext(), "카테고리가 만들어 졌습니다.", Toast.LENGTH_SHORT).show()

                            } else {
                                Log.e("카테고리 만들기", "실패: ${response.code()}")
                                Toast.makeText(requireContext(), "error", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<MyStudyResponse>, t: Throwable) {
                            Log.e("카테고리 만들기", "${t.localizedMessage}")
                        }
                    })
                } else {
                    Toast.makeText(requireContext(), "카테고리 이름이 비었습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            // "Cancel" 버튼 추가
            builder.setNegativeButton("취소") { dialog, _ ->
                Toast.makeText(requireContext(), "취소했습니다.", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

            // 다이얼로그 생성 및 표시
            val alertDialog = builder.create()
            alertDialog.show()
        }

        //내 그룹 표시
        serviceMyGroup.showMyGroup().enqueue(object : Callback<List<GroupGenerateResponse>> {
            override fun onResponse(call: Call<List<GroupGenerateResponse>>, response: Response<List<GroupGenerateResponse>>) {
                if (response.isSuccessful) {
                    val myGroup = response.body()

                    if (myGroup != null && myGroup.isNotEmpty()) {
                        myGroupAdapter.setMyGroup(myGroup)
                        myGroupAdapter.notifyDataSetChanged()
                    }
                    Log.d("내 그룹 불러오기", "성공 : $myGroup")
                } else {
                    Log.e("내 그룹 불러오기", "실패: ${response.code()}")
                    Toast.makeText(requireContext(), "내 그룹 불러오기 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<GroupGenerateResponse>>, t: Throwable) {
                Log.e("내 그룹 불러오기", "${t.localizedMessage}")
                Toast.makeText(requireContext(), "네트워크 오류", Toast.LENGTH_SHORT).show()
            }
        })


        //timer 클릭시
        timer.setOnClickListener {
            if (!timerRunning) {
                startTimer()
                // WebSocket 연결 시작
                connectWebSocket(jwtToken!!)
            } else {
                showSaveDialog()
            }
        }

        //탭 레이아웃 변경 (내공부, 속한 그룹)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    when (tab.position) {
                        0 -> {
                            layoutMyStudy.visibility = View.VISIBLE
                            layoutGroupStudy.visibility = View.GONE
                        }

                        1 -> {
                            layoutMyStudy.visibility = View.GONE
                            layoutGroupStudy.visibility = View.VISIBLE
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })


        return view
    }
    //현재 날짜 가져오기
    private fun getCurrentFormattedDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd (E)", Locale.KOREA)
        return dateFormat.format(calendar.time)
    }

    //리사이클러뷰 클릭 시 그룹 id 보내기
    private fun navigateToGroupDetail(groupId: String) {
        val intent = Intent(requireContext(), GroupMemberTimerActivity::class.java)
        intent.putExtra("id", groupId)
        startActivity(intent)
    }
    //초를 00:00:00로 변환
    private fun convertSecondsToFormattedTime(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val remainingSeconds = seconds % 60

        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
    }

    private fun startTimer() {
        timerRunning = true

        timerHandler = Handler()
        timerRunnable = object : Runnable {
            override fun run() {
                seconds++
                updateTimerText()
                timerHandler.postDelayed(this, 1000)
            }
        }

        timerHandler.postDelayed(timerRunnable, 1000)


    }
    private fun updateTimerText() {
        val formattedTime = String.format(
            Locale.getDefault(),
            "%02d:%02d:%02d",
            seconds / 3600,
            (seconds % 3600) / 60,
            seconds % 60
        )
        timer.text = formattedTime
    }
    private fun showSaveDialog() {
        // Stop the timer
        timerHandler.removeCallbacks(timerRunnable)
        timerRunning = false

        // Create and show the save dialog
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("저장")
        builder.setMessage("공부 시간을 저장하시겠습니까?")

        builder.setPositiveButton("확인") { _, _ ->
            // 추가된 부분: 웹소켓 연결이 되어 있다면 연결 종료
            if (isWebSocketConnected) {
                myWebSocketListener.closeWebSocket()
                isWebSocketConnected = false
            }

            showStudyTime(seconds)
            updateTimerText()
        }

        builder.setNegativeButton("취소") { dialog, _ ->
            // Resume the timer
            startTimer()
            dialog.dismiss()
        }

        builder.show()
    }

    private fun showStudyTime(studyTimeInSeconds: Int) { //(studyTimeInSeconds: Int, service: MyInfoInterface)
        // 로컬 데이터베이스에 공부 시간을 저장하는 로직을 구현
        Log.d("시간 저장하기", "저장할 시간 (단위: 초): $studyTimeInSeconds seconds")
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
                .url(SW_SERVER_URL)
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
            val studyStartMessage = JSONObject().apply {
                put("type", "STUDY_START")
                put("accessToken", accessToken)
                put("subjectId", 5)
            }

            sendMessage(studyStartMessage.toString())
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            // 서버로부터 텍스트 메시지를 수신했을 때 호출
            // 메시지를 파싱하고 UI를 업데이트하는 작업 수행
            Log.d("WebSocket", "서버에서 메시지 수신: $text")
            // 추가된 부분: 메시지 파싱 및 처리
            try {
                val jsonResponse = JSONObject(text)
                val result = jsonResponse.getString("result")
                val message = jsonResponse.getString("message")

                // 처리 결과에 따른 동작 수행
                if (result == "OK") {
                    Log.d("WebSocket", "서버 응답: $message")
                    // 처리 성공에 대한 추가 동작 수행
                } else {
                    Log.e("WebSocket", "서버 응답 실패: $message")
                    // 처리 실패에 대한 추가 동작 수행
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


    companion object {
        const val SW_SERVER_URL = "wss://dadogk2.duckdns.org/study/connect"
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}