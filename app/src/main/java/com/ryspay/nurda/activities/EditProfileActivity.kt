package com.ryspay.nurda.activities

import PasswordDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.ryspay.nurda.R
import com.ryspay.nurda.models.User
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlin.math.log

class EditProfileActivity : AppCompatActivity(), View.OnClickListener, PasswordDialog.Listener {
    private val TAG = "EditProfileActivity"
    private lateinit var mUser: User
    private lateinit var mPendingUser: User

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        Log.d(TAG, "onCreate: ");

        close_image.setOnClickListener (this)
        save_image.setOnClickListener(this)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference

        mDatabase.child("users").child(mAuth.currentUser!!.uid)
            .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                mUser = it.getValue(User::class.java)!!
                name_input.setText(mUser.name, TextView.BufferType.EDITABLE)
                username_input.setText(mUser.username, TextView.BufferType.EDITABLE)
                web_input.setText(mUser.website, TextView.BufferType.EDITABLE)
                bio_input.setText(mUser.bio, TextView.BufferType.EDITABLE)
                email_input.setText(mUser.email, TextView.BufferType.EDITABLE)
                number_input.setText(mUser.phone, TextView.BufferType.EDITABLE)
                gender_input.setText(mUser.gender, TextView.BufferType.EDITABLE)
            })
    }
    private fun updateProfile(){
        // get user from inputs
        mPendingUser = User(
            name = name_input.text.toString(),
            username = username_input.text.toString(),
            website = web_input.text.toString(),
            bio = bio_input.text.toString(),
            email = email_input.text.toString(),
            phone = number_input.text.toString(),
            gender = gender_input.text.toString()
        )
        //validate
        val error = validate(mPendingUser)
        if(error==null){
            //save mPendingUser
            if(mPendingUser.email == mUser.email){
                //update mPendingUser
                updateUser(mPendingUser)
            }else{
                // re-enter password
                PasswordDialog().show(supportFragmentManager,"password_dialog")
            }
        }else{
            showToast(error)
        }
    }

    private fun updateUser(user: User){
        val updateMap = mutableMapOf<String, Any>()

        if(user.bio != mUser.bio) updateMap["bio"] = user.bio
        if(user.email != mUser.email) updateMap["email"] = user.email
        if(user.gender != mUser.gender) updateMap["gender"] = user.gender
        if(user.name != mUser.name) updateMap["name"] = user.name
        if(user.phone != mUser.phone) updateMap["phone"] = user.phone
        if(user.username != mUser.username) updateMap["username"] = user.username
        if(user.website != mUser.website) updateMap["website"] = user.website

        mDatabase.child("users").child(mAuth.currentUser!!.uid).updateChildren(updateMap).addOnCompleteListener {
            if(it.isSuccessful){
                showToast("Profile saved")
                finish()
            }else{
                showToast(it.exception!!.message!!)
            }
        }
    }

    override fun onPasswordConfirm(password: String) {
        // re-auth
        val credential = EmailAuthProvider.getCredential(mUser.email, password)
        mAuth.currentUser!!.reauthenticate(credential).addOnCompleteListener {
            if(it.isSuccessful){
                Log.d(TAG, "onPasswordConfirm: success faze 1")
                // update email in auth
                mAuth.currentUser!!.updateEmail(mPendingUser.email).addOnCompleteListener {
                    if(it.isSuccessful){
                        // update user
                        updateUser(mPendingUser)
                        Log.d(TAG, "onPasswordConfirm: success faze 2")
                    }else{
                        Log.d(TAG, "onPasswordConfirm: failure faze 1")
                        Log.d(TAG, "onPasswordConfirm: "+ it.exception!!.message)
                        showToast(it.exception!!.message!!)
                    }
                }
            }else{
                showToast(it.exception!!.message!!)
                Log.d(TAG, "onPasswordConfirm: failure faze 2")
            }
        }
    }

    private fun validate(user: User): String?  = when {
            user.name.isEmpty() -> "Please enter your namae"
            user.username.isEmpty() -> "Please enter your username"
            user.email.isEmpty() -> "Please enter your email"
            else -> null
        }

    override fun onClick(view: View) {
        when(view.id){
            R.id.save_image ->{
                updateProfile()
            }
            R.id.close_image ->{
                finish()
                overridePendingTransition(0,0)
            }
        }
    }
}



