package com.github.da_dogk.activities.group

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.github.da_dogk.server.interface_folder.GetGroupGenerateInterface
import com.github.da_dogk.server.response.GroupGenerateResponse
import com.google.android.material.tabs.TabLayout
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * A simple [Fragment] subclass.
 * Use the [GroupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GroupFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var tabLayout: TabLayout
    lateinit var layoutGroup: FrameLayout
    lateinit var layoutSchool: ConstraintLayout
    lateinit var buttonWritePost : ImageButton

    lateinit var recyclerView : RecyclerView
    lateinit var groupAdapter: GroupAdapter

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

        recyclerView = view.findViewById(R.id.rv_group)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        groupAdapter = GroupAdapter() // Create this adapter class
        recyclerView.adapter = groupAdapter




        val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val jwtToken = sharedPreferences.getString("accessToken", "")

        val retrofit = Retrofit.Builder()
            .baseUrl("https://dadogk.duckdns.org/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient(jwtToken))
            .build()

        val service = retrofit.create(GetGroupGenerateInterface::class.java)


        service.getGroup("Bearer $jwtToken").enqueue(object : Callback<List<GroupGenerateResponse>> {
            override fun onResponse(call: Call<List<GroupGenerateResponse>>, response: Response<List<GroupGenerateResponse>>) {
                if (response.isSuccessful) {
                    val group = response.body()
                    // categories를 사용하여 원하는 작업을 수행
                    if (group != null && group.isNotEmpty()) {
                        groupAdapter.setGroups(group)


                        // 예를 들어, RecyclerView에 데이터를 설정하는 등의 작업을 수행
//                        categoryName.text = categories[0].title
//
//                        // RecyclerView에 데이터 설정
//                        myStudyAdapter.setData(categories)
//                        myStudyAdapter.notifyDataSetChanged()

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