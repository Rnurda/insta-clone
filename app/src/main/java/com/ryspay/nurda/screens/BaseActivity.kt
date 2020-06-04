package com.ryspay.nurda.screens

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.bottom_navigation_view.*

abstract class BaseActivity: AppCompatActivity(){

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }


    override fun onResume() {
        super.onResume()
        if(bottom_navigation_view!=null) {
            bottom_navigation_view.menu.getItem(navNumber).isChecked = true
        }
    }

    companion object{
        const val TAG = "BaseActivity"
    }

}