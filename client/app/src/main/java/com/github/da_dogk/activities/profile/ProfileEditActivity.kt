package com.github.da_dogk.activities.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.github.da_dogk.R

class ProfileEditActivity : AppCompatActivity() {
    lateinit var goBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)
        goBack = findViewById(R.id.b_go_back)

        goBack.setOnClickListener {
            onBackPressed()
        }
    }
}