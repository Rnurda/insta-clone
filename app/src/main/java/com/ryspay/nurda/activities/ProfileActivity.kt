package com.ryspay.nurda.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.ryspay.nurda.R
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : BaseActivity(4) {
    private val TAG = "ProfileActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setUpBottomNavigation()
        Log.d(TAG, "onCreate: ")

        edit_profile_btn.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java);
            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            startActivity(intent)
            overridePendingTransition(0,0)
        }

    }
}
