package com.github.da_dogk.activities.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.github.da_dogk.R
import com.google.android.material.tabs.TabLayout

class HomeFragment : Fragment() {

    lateinit var tabLayout: TabLayout
    lateinit var layoutMyStudy: LinearLayout
    lateinit var layoutGroupStudy: LinearLayout


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
        layoutMyStudy = view.findViewById(R.id.LL_myStudy)
        layoutGroupStudy = view.findViewById(R.id.LL_groupStudy)

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

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}