package com.ryspay.nurda.screens.common

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

abstract class BaseActivity: AppCompatActivity(){
    protected lateinit var commonViewModel: CommonViewModel

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        commonViewModel = ViewModelProvider(this).get(CommonViewModel::class.java)
        commonViewModel.errorMessage.observe(this, Observer { it?.let {
            showToast(it)
        }})
    }

    protected inline fun <reified T : ViewModel> initViewModel() : T =
        ViewModelProvider(this,
            ViewModelFactory(commonViewModel)
        ).get(T::class.java)

    companion object{
        const val TAG = "BaseActivity"
    }
    
}