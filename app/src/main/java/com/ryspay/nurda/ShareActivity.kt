package com.ryspay.nurda

import android.os.Bundle
import android.util.Log

class ShareActivity : BaseActivity(2) {
    private val TAG = "ShareActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setUpBottomNavigation()
        Log.d(TAG, "onCreate: ")
    }
}
