package com.github.da_dogk.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.github.da_dogk.R
import com.github.da_dogk.TAG_GROUP
import com.github.da_dogk.TAG_HOME
import com.github.da_dogk.TAG_MY_PROFILE
import com.github.da_dogk.activities.fragment.GroupFragment
import com.github.da_dogk.activities.fragment.HomeFragment
import com.github.da_dogk.activities.fragment.MyProfileFragment
import com.github.da_dogk.databinding.ActivityNaviBinding

class NaviActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = ActivityNaviBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setFragment(TAG_HOME, HomeFragment())

        binding.navigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.GroupFragment -> setFragment(TAG_GROUP, GroupFragment())
                R.id.HomeFragment -> setFragment(TAG_HOME, HomeFragment())
                R.id.MyProfileFragment -> setFragment(TAG_MY_PROFILE, MyProfileFragment())
            }
            true
        }
    }

    private fun setFragment(tag: String, fragment: Fragment) {
        val manager: FragmentManager = supportFragmentManager
        val fragTransaction = manager.beginTransaction()

        if (manager.findFragmentByTag(tag) == null){
            fragTransaction.add(R.id.mainFrameLayout, fragment, tag)
        }

        val group = manager.findFragmentByTag(TAG_GROUP)
        val home = manager.findFragmentByTag(TAG_HOME)
        val myProfile = manager.findFragmentByTag(TAG_MY_PROFILE)

        if (group != null){
            fragTransaction.hide(group)
        }

        if (home != null){
            fragTransaction.hide(home)
        }

        if (myProfile != null) {
            fragTransaction.hide(myProfile)
        }

        if (tag == TAG_GROUP) {
            if (group!=null){
                fragTransaction.show(group)
            }
        }
        else if (tag == TAG_HOME) {
            if (home != null) {
                fragTransaction.show(home)
            }
        }

        else if (tag == TAG_MY_PROFILE){
            if (myProfile != null){
                fragTransaction.show(myProfile)
            }
        }

        fragTransaction.commitAllowingStateLoss()
    }
}