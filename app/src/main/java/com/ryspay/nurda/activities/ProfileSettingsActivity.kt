package com.ryspay.nurda.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ryspay.nurda.R
import com.ryspay.nurda.utils.FirebaseHelper
import kotlinx.android.synthetic.main.activity_profile_settings.*

class ProfileSettingsActivity : AppCompatActivity(){
    private lateinit var mFirebase: FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFirebase = FirebaseHelper(this)
        setContentView(R.layout.activity_profile_settings)
        sign_out_text.setOnClickListener { mFirebase.auth.signOut() }
        back_image.setOnClickListener { finish() }
    }
}
