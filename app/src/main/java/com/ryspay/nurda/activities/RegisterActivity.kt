package com.ryspay.nurda.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.ryspay.nurda.R
import com.ryspay.nurda.models.User
import kotlinx.android.synthetic.main.fragment_register_email.*
import kotlinx.android.synthetic.main.fragment_register_email.email_input
import kotlinx.android.synthetic.main.fragment_register_namepass.*
import kotlinx.android.synthetic.main.fragment_register_namepass.password_input

class RegisterActivity : AppCompatActivity(), EmailFragment.Listener, NamePassFragment.Listener{
    private val TAG = "RegisterActivity"

    private var mEmail: String? = null

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference

        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction().add(R.id.frame_layout, EmailFragment()).commit()
        }
    }

        override fun onNext(email: String) {
            if(email.isNotEmpty()){
                //go next
                mEmail = email
                mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener {
                    if (it.isSuccessful) {
                        if (it.result?.signInMethods?.isEmpty() != false) {
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.frame_layout, NamePassFragment())
                                .addToBackStack(null)
                                .commit()
                        }else{
                            showToast("This email address already uses")
                        }
                    }else{
                        showToast(it.exception!!.message!!)
                    }
                }
            }else{
                showToast("Please enter email")
            }
        }

        override fun onRegister(fullName: String, password: String) {
            if(fullName.isNotEmpty()&&password.isNotEmpty()){
                //register
                val email = mEmail
                if(email !=null) {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if(it.isSuccessful){
                            val user = mkUser(fullName, email)
                            val reference = mDatabase.child("users").child(it.result!!.user!!.uid)
                            reference.setValue(user).addOnCompleteListener {
                                if(it.isSuccessful){
                                    startyHomeActivity()
                                }else{
                                    unknownRegisterError(it)
                                }
                            }
                        }else{
                            unknownRegisterError(it)
                        }
                    }
                }else{
                    Log.e(TAG, "onRegister: email is null")
                    showToast("Please enter email")
                    supportFragmentManager.popBackStack()
                }
            }else{
                showToast("Please enter full name and password")
            }
        }

    private fun unknownRegisterError(it: Task<*>) {  //"out Any" or "*" if don't metter type (Если не имеет значения)
        Log.e(TAG, "Failed to create user", it.exception)
        showToast("Something wrong happend, Please try again later)")
    }

    private fun startyHomeActivity() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun mkUser(fullName: String,email: String): User{
        var username = makeUsername(fullName)
        return User(name = fullName, username = username, email = email)
    }
    private fun makeUsername(fullName: String) =
        fullName.toLowerCase().replace(" ",".")

}
    // 1 - Email, next btn
    class EmailFragment: Fragment(){
        private lateinit var mListener:Listener
        interface Listener{
            fun onNext(email:String)
        }/**/

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return inflater.inflate(R.layout.fragment_register_email, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            coordinateBtnAndInputs(next_btn,email_input)
            next_btn.setOnClickListener{
                val email = email_input.text.toString()
                mListener.onNext(email)

            }
        }

        override fun onAttach(context: Context) {
            super.onAttach(context)
            mListener = context as Listener
        }
    }

    //2 - Full name, password, registation btn
    class NamePassFragment: Fragment(){
        private lateinit var mListener: Listener
        interface Listener{
            fun onRegister(fullName: String, password: String)
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return inflater.inflate(R.layout.fragment_register_namepass, container, false)
        }
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            coordinateBtnAndInputs(register_btn,full_name_input,password_input)
            register_btn.setOnClickListener {
                val name = full_name_input.text.toString()
                val password = password_input.text.toString()
                mListener.onRegister(name,password)
            }
        }

        override fun onAttach(context: Context) {
            super.onAttach(context)
            mListener = context as Listener
        }

    }