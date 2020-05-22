package com.ryspay.nurda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

class LoginActivity : AppCompatActivity(), KeyboardVisibilityEventListener, TextWatcher,
    View.OnClickListener {
    private val TAG = "LoginActivity"
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.d(TAG, "onCreate: ")

        KeyboardVisibilityEvent.setEventListener(this,this)
        login_btn.isEnabled = false
        email_input.addTextChangedListener(this)
        password_input.addTextChangedListener(this)
        login_btn.setOnClickListener(this)

        mAuth = FirebaseAuth.getInstance()
    }

    override fun onClick(view: View) {
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
            Toast.makeText(this, "please enter email and passoword",Toast.LENGTH_SHORT).show()
        }
    }

    override fun afterTextChanged(p0: Editable?) {
       login_btn.isEnabled = validate(email_input.text.toString(), password_input.text.toString())
    }

    private fun validate(email: String, password: String) = email.isNotEmpty() && password.isNotEmpty()

    override fun onVisibilityChanged(isKeyboardOpen: Boolean) {
        if(isKeyboardOpen){
            create_account_text.visibility = View.GONE
            scrollView.scrollTo(0, scrollView.bottom)

        }else{
            scrollView.scrollTo(0, scrollView.top)
            create_account_text.visibility = View.VISIBLE
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}


}
