package com.ryspay.nurda

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.d(TAG, "onCreate: ")


    }

}
