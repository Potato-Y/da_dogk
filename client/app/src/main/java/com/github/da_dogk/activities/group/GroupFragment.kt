package com.github.da_dogk.activities.group

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
//import com.github.da_dogk.ARG_PARAM1
//import com.github.da_dogk.ARG_PARAM2
import com.github.da_dogk.R
import com.github.da_dogk.adapter.GroupAdapter
import com.github.da_dogk.server.RetrofitClient
import com.github.da_dogk.server.interface_folder.GroupGenerateInterface
import com.github.da_dogk.server.interface_folder.SchoolEmailInterface
import com.github.da_dogk.server.request.MyStudyRequest
import com.github.da_dogk.server.request.SchoolEmailRequest
import com.github.da_dogk.server.request.SchoolNumberRequest
import com.github.da_dogk.server.response.GroupGenerateResponse
import com.github.da_dogk.server.response.MyStudyResponse
import com.github.da_dogk.server.response.SchoolEmailResponse
import com.google.android.material.tabs.TabLayout
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type

/**
 * A simple [Fragment] subclass.
 * Use the [GroupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GroupFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    //그룹 부분 변수
    lateinit var tabLayout: TabLayout
    lateinit var layoutGroup: FrameLayout
    lateinit var layoutSchool: ConstraintLayout
    lateinit var buttonWritePost : ImageButton

    //리사이클러뷰
    lateinit var recyclerView : RecyclerView
    lateinit var groupAdapter: GroupAdapter

    //학교 부분 변수
    lateinit var schoolEmail: EditText
    lateinit var buttonCertify: Button
    lateinit var certifyNumber: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            //param1 = it.getString(ARG_PARAM1)
            //param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_group, container, false)

        tabLayout = view.findViewById(R.id.tab_layout_group)
        layoutGroup = view.findViewById(R.id.FL_group)
        layoutSchool = view.findViewById(R.id.CL_school)

        //리사이클러뷰 설정
        recyclerView = view.findViewById(R.id.rv_group)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        groupAdapter = GroupAdapter()
        recyclerView.adapter = groupAdapter

        //학교 인증 버튼
        buttonCertify = view.findViewById(R.id.b_certify)


        val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val jwtToken = sharedPreferences.getString("accessToken", "")


        val retrofit = RetrofitClient.createRetrofitInstance(jwtToken)

        val service = retrofit.create(GroupGenerateInterface::class.java)
        val serviceSchool = retrofit.create(SchoolEmailInterface::class.java)


        //생성된 그룹들 보여주기
        service.showGroup("Bearer $jwtToken").enqueue(object : Callback<List<GroupGenerateResponse>> {
            override fun onResponse(call: Call<List<GroupGenerateResponse>>, response: Response<List<GroupGenerateResponse>>) {
                if (response.isSuccessful) {
                    val group = response.body()
                    // categories를 사용하여 원하는 작업을 수행
                    if (group != null && group.isNotEmpty()) {
                        groupAdapter.setGroups(group)
                    }
                    Log.d("글 불러오기", "성공 : $group")
                } else {
                    Log.e("글 불러오기", "실패: ${response.code()}")
                    Toast.makeText(requireContext(), "글 불러오기 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<GroupGenerateResponse>>, t: Throwable) {
                Log.e("글 불러오기", "${t.localizedMessage}")
                Toast.makeText(requireContext(), "네트워크 오류", Toast.LENGTH_SHORT).show()
            }
        })

        //버튼 클릭시 학교인증 메일 보내기
        buttonCertify.setOnClickListener {
            schoolEmail = view.findViewById(R.id.ET_school_email)

            val school = schoolEmail.text.toString()
            val request = SchoolEmailRequest(school)

            serviceSchool.sendEmail("Bearer $jwtToken",request).enqueue(object : Callback<SchoolEmailResponse>{
                override fun onResponse(call: Call<SchoolEmailResponse>, response: Response<SchoolEmailResponse>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        Log.d("학교 인증 번호 보내기", "${result}")
                        Toast.makeText(requireContext(), "인증번호를 보냈습니다.", Toast.LENGTH_SHORT).show()

                        val inflater = LayoutInflater.from(requireContext())
                        val dialogView = inflater.inflate(R.layout.dialog1, null)
                        certifyNumber = dialogView.findViewById(R.id.ET_input_title)

                        val builder = AlertDialog.Builder(requireContext())
                        builder.setView(dialogView)
                        builder.setTitle("인증 번호 입력")

                        builder.setPositiveButton("확인") { _, _ ->
                            val edit = certifyNumber.text.toString()
                            val request = SchoolNumberRequest(edit)

                            if(edit.isNotEmpty()){
                                serviceSchool.sendNumber("Bearer $jwtToken",request).enqueue(object : Callback<SchoolEmailResponse>{
                                    override fun onResponse(call: Call<SchoolEmailResponse>, response: Response<SchoolEmailResponse>) {
                                        if (response.isSuccessful) {
                                            val result = response.body()
                                            Log.d("학교인증 ", "${result}")
                                            Toast.makeText(requireContext(), "학교인증 성공", Toast.LENGTH_SHORT).show()

                                        } else {
                                            Log.e("학교인증", "실패: ${response.code()}")
                                            Toast.makeText(requireContext(), "error", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    override fun onFailure(call: Call<SchoolEmailResponse>, t: Throwable) {
                                        Log.e("학교인증", "${t.localizedMessage}")
                                    }
                                })
                            } else {
                                Toast.makeText(requireContext(), "코드를 입력하지 않았습니다.", Toast.LENGTH_SHORT).show()
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

                    } else {
                        Log.e("학교 인증 번호 보내기", "실패: ${response.code()}")
                        Toast.makeText(requireContext(), "error", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<SchoolEmailResponse>, t: Throwable) {
                    Log.e("학교 인증 번호 보내기", "${t.localizedMessage}")
                }
            })
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    when (tab.position) {
                        0 -> {
                            layoutGroup.visibility = View.VISIBLE
                            layoutSchool.visibility = View.GONE
                        }

                        1 -> {
                            layoutGroup.visibility = View.GONE
                            layoutSchool.visibility = View.VISIBLE
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })


        buttonWritePost = view.findViewById(R.id.B_write_post1)

        buttonWritePost.setOnClickListener {
            activity?.let{
                val intent = Intent(context, GroupGenerateActivity::class.java)
                startActivity(intent)
            }
        }


        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GroupFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GroupFragment().apply {
                arguments = Bundle().apply {
                    //putString(ARG_PARAM1, param1)
                    //putString(ARG_PARAM2, param2)
                }
            }
    }

}