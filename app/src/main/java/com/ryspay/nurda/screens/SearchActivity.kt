package com.ryspay.nurda.screens

import android.os.Bundle
import android.util.Log
import com.ryspay.nurda.R
import com.ryspay.nurda.screens.common.BaseActivity
import com.ryspay.nurda.screens.common.CameraHelper
import com.ryspay.nurda.views.setUpBottomNavigation

class SearchActivity : BaseActivity() {
    private val TAG = "SearchActivity"
    private lateinit var mCameraHelper: CameraHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setUpBottomNavigation(1)
        Log.d(TAG, "onCreate: ")
    }


}
