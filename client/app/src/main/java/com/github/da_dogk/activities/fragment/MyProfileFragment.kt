package com.github.da_dogk.activities.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
//import com.github.da_dogk.ARG_PARAM1
//import com.github.da_dogk.ARG_PARAM2
import com.github.da_dogk.activities.ProfileEditActivity
import com.github.da_dogk.R

/**
 * A simple [Fragment] subclass.
 * Use the [MyProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var buttonEdit : Button

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
        buttonEdit.setOnClickListener {
            activity?.let{
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
}