package com.github.da_dogk.activities.fragment

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.github.da_dogk.R
import com.github.da_dogk.TAG_GROUP
import com.github.da_dogk.TAG_HOME
import com.github.da_dogk.TAG_MY_PROFILE
import com.github.da_dogk.activities.group.GroupFragment
import com.github.da_dogk.activities.home.HomeFragment
import com.github.da_dogk.activities.profile.MyProfileFragment
import com.github.da_dogk.databinding.ActivityNaviBinding

class NaviActivity : AppCompatActivity() {
override fun onCreate(savedInstanceState: Bundle?) {
super.onCreate(savedInstanceState)
var binding = ActivityNaviBinding.inflate(layoutInflater)
setContentView(binding.root)

        // MainActivity에서 토큰이 전달되었는지 확인
        if (intent.hasExtra(EXTRA_ACCESS_TOKEN)) {
            val accessToken = intent.getStringExtra(EXTRA_ACCESS_TOKEN)
            Log.d("토큰 전달 확인", "MainActivity에서 토큰이 전달 성공")
            // 이제 accessToken을 사용하여 NaviActivity에서 필요한 대로 사용할 수 있습니다.
        } else {
            // MainActivity에서 토큰이 전달되지 않은 경우 처리
            Log.e("토큰 전달 확인", "MainActivity에서 토큰이 전달되지 않았습니다.")
        }


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

    companion object {
        const val EXTRA_ACCESS_TOKEN = "extra_access_token"
        //const val EXTRA_USER = "extra_user"
    }
}