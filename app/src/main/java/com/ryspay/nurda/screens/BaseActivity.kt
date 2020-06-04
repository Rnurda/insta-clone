package com.ryspay.nurda.screens

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.bottom_navigation_view.*

abstract class BaseActivity: AppCompatActivity(){
    private lateinit var commonViewModel: CommonViewModel

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        commonViewModel =ViewModelProvider(this).get(CommonViewModel::class.java)
    }

    inline fun <reified T : ViewModel> initViewModel() : T =
        ViewModelProvider(this, ViewModelFactory()).get(T::class.java)

    companion object{
        const val TAG = "BaseActivity"
    }
    
}