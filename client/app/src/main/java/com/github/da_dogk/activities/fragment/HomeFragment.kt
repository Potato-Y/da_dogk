package com.github.da_dogk.activities.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.da_dogk.R
import com.github.da_dogk.adapter.Data
import com.github.da_dogk.adapter.MyStudyAdapter
import com.github.da_dogk.server.interface_folder.LoginInterface
import com.github.da_dogk.server.interface_folder.MyStudyInterface
import com.github.da_dogk.server.request.MyStudyRequest
import com.github.da_dogk.server.response.MyStudyResponse
import com.google.android.material.tabs.TabLayout
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment : Fragment() {

    lateinit var tabLayout: TabLayout
    lateinit var layoutMyStudy: FrameLayout
    lateinit var layoutGroupStudy: LinearLayout
    lateinit var buttonAddCategory: ImageButton
    lateinit var editTextInputTitle: EditText
    lateinit var recycleMyStudy: RecyclerView

//    //이걸 MyStudyAdapter에 넣어도 됨
//    val DataList = arrayListOf(
//        Data(R.drawable.arrow_left, "0번"),
//        Data(R.drawable.arrow_left, "1번"),
//        Data(R.drawable.arrow_left, "2번"),
//    )


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
        layoutGroupStudy = view.findViewById(R.id.LL_groupStudy)
        buttonAddCategory = view.findViewById(R.id.B_add_category)
        recycleMyStudy = view.findViewById(R.id.RV_myStudy)

        //내공부 리사이클러뷰 설정
//        recycleMyStudy.layoutManager = LinearLayoutManager(requireContext())
//        recycleMyStudy.adapter = MyStudyAdapter(DataList)


        val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val jwtToken = sharedPreferences.getString("accessToken", "")

        val retrofit = Retrofit.Builder()
            .baseUrl("https://dadogk.duckdns.org/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient(jwtToken))
            .build()

        val service = retrofit.create(MyStudyInterface::class.java)


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


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}