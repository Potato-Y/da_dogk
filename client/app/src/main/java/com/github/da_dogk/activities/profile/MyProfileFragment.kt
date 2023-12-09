package com.github.da_dogk.activities.profile

import android.app.AlertDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

import androidx.fragment.app.Fragment

import com.github.da_dogk.R
import com.github.da_dogk.activities.login.MainActivity
import com.github.da_dogk.server.RetrofitClient
import com.github.da_dogk.server.interface_folder.MyInfoInterface

import com.github.da_dogk.server.response.User

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * A simple [Fragment] subclass.
 * Use the [MyProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var buttonEdit: Button
    lateinit var profileName: TextView
    lateinit var signout: TextView

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
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_my_profile, container, false)

        buttonEdit = view.findViewById(R.id.B_profile_edit)
        profileName = view.findViewById(R.id.T_profile_name)
        signout = view.findViewById(R.id.T_signout)

        val sharedPreferences =
            requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val jwtToken = sharedPreferences.getString("accessToken", "")

        val retrofit = RetrofitClient.createRetrofitInstance(jwtToken)

        val service = retrofit.create(MyInfoInterface::class.java)

        service.showMyInfo("Bearer $jwtToken").enqueue(object :
            Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                val user = response.body()
                profileName.text = user?.nickname
            }

            override fun onFailure(call: Call<User>, t: Throwable) {

            }
        })

        //로그아웃
        signout.setOnClickListener {
            showLogoutDialog()
        }

        buttonEdit.setOnClickListener {
            activity?.let {
                val intent = Intent(context, ProfileEditActivity::class.java)
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
         * @return A new instance of fragment MyProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyProfileFragment().apply {
                arguments = Bundle().apply {
                    //putString(ARG_PARAM1, param1)
                    //putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun showLogoutDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("로그아웃")
            .setMessage("로그아웃 하시겠습니까?")
            .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                logout()
            })
            .setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->
                // 취소 버튼을 눌렀을 때 아무 동작 없음
            })
            .show()
    }

    private fun logout() {
        // 사용자 토큰 삭제
        clearToken()

        // 로그아웃 후 로그인 화면으로 이동
        Intent(activity, MainActivity::class.java).apply {
            startActivity(this)
            activity?.finish() // 현재 액티비티를 종료하여 뒤로 가기 시 로그인 화면으로 돌아가지 않도록 함
        }
    }

    private fun clearToken() {
        // 토큰 삭제
        val sharedPreferences: SharedPreferences =
            requireActivity().getSharedPreferences("user_prefs", MODE_PRIVATE)
        sharedPreferences.edit().remove("accessToken").apply()
    }
}