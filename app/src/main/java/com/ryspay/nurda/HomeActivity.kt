package com.ryspay.nurda

import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : BaseActivity(0) {
    private val TAG = "HomeActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setUpBottomNavigation()
        Log.d(TAG, "onCreate: ")

        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword("admin@gmail.com","password")
            .addOnCompleteListener {
                if(it.isSuccessful){
                    Log.d(TAG, "Signin sucesss")
                }else{
                    Log.d(TAG, "Signin failure", it.exception)
                }
            }
    }
}
