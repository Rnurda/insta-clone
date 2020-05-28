package com.ryspay.nurda.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.ryspay.nurda.R
import kotlinx.android.synthetic.main.activity_login.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

class LoginActivity : AppCompatActivity(), KeyboardVisibilityEventListener,
    View.OnClickListener {
    private val TAG = "LoginActivity"
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.d(TAG, "onCreate: ")

        KeyboardVisibilityEvent.setEventListener(this,this)
        coordinateBtnAndInputs(login_btn,email_input,password_input)
        login_btn.setOnClickListener(this)
        create_account_text.setOnClickListener(this)

        mAuth = FirebaseAuth.getInstance()
    }

    override fun onClick(view: View) {
        when(view.id){
            R.id.create_account_text ->{
                startActivity(Intent(this, RegisterActivity::class.java))
            }
            R.id.login_btn ->{
                val email = email_input.text.toString()
                val password = password_input.text.toString()
                if(validate(email,password)) {
                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                        if(it.isSuccessful){
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        }
                    }
                }else{
                    showToast(getString(R.string.please_enter_your_email_and_password))
                }
            }
        }
    }

    private fun validate(email: String, password: String) = email.isNotEmpty() && password.isNotEmpty()
    override fun onVisibilityChanged(isKeyboardOpen: Boolean) {
        if(isKeyboardOpen){
            create_account_text.visibility = View.GONE
        }else{
            create_account_text.visibility = View.VISIBLE
        }
    }
}
