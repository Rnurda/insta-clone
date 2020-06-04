package com.ryspay.nurda.screens

import android.os.Bundle
import android.util.Log
import com.ryspay.nurda.R
import com.ryspay.nurda.utils.CameraHelper

class SearchActivity : BaseActivity(1) {
    private val TAG = "SearchActivity"
    private lateinit var mCameraHelper: CameraHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setUpBottomNavigation()
        Log.d(TAG, "onCreate: ")
    }


}
