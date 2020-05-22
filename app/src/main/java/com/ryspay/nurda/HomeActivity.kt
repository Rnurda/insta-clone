package com.ryspay.nurda

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity(0) {
    private val TAG = "HomeActivity"
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setUpBottomNavigation()
        Log.d(TAG, "onCreate: ")

        mAuth = FirebaseAuth.getInstance()
        /*auth.signInWithEmailAndPassword("admin@gmail.com","password")
            .addOnCompleteListener {
                if(it.isSuccessful){
                    Log.d(TAG, "Signin sucesss")
                }else{
                    Log.d(TAG, "Signin failure", it.exception)
                }
            }*/
        sign_out_text.setOnClickListener {
            mAuth.signOut()
        }
        mAuth.addAuthStateListener {
            if(it.currentUser == null){
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if(mAuth.currentUser == null){
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
