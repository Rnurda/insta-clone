package com.ryspay.nurda.screens

import android.os.Bundle
import android.util.Log
import com.ryspay.nurda.R
import com.ryspay.nurda.views.setUpBottomNavigation

class LikesActivity : BaseActivity() {
    private val TAG = "LikesActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setUpBottomNavigation(3)
        Log.d(TAG, "onCreate: ")
    }
}
