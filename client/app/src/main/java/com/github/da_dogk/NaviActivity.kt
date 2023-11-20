package com.github.da_dogk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.github.da_dogk.databinding.ActivityNaviBinding

private const val TAG_HOME = "home_fragment"
private const val TAG_GROUP = "group_fragment"
private const val TAG_MY_PROFILE = "my_profile_fragment"

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