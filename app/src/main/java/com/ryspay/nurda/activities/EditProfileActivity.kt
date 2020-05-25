package com.ryspay.nurda.activities

import PasswordDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.ryspay.nurda.R
import com.ryspay.nurda.models.User
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlin.math.log
import com.google.android.gms.tasks.OnSuccessListener as OnSuccessListener1

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

    override fun onPasswordConfirm(password: String) {
        if(password.isNotEmpty()) {
            // re-auth
            val credential = EmailAuthProvider.getCredential(mUser.email, password)
            mAuth.currentUser!!.reauthenticate(credential){
                // update email in auth
                mAuth.currentUser!!.updateEmail(mPendingUser.email){
                    // update user
                    updateUser(mPendingUser)
                }
            }
        }else{
            showToast("You should enter wrong password")
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
       showToast("Profile saved")
       finish()

       mDatabase.updateUser(mAuth.currentUser!!.uid,updateMap){
               showToast("Profile saved")
               finish()
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

    private fun FirebaseUser.updateEmail(email: String, onSuccessListener: () ->  Unit){
        updateEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccessListener()
            } else {
                //showToast(it.exception!!.message!!)
                showToast("You enter wrong password")
                Log.d(TAG, "onPasswordConfirm: failure faze 2")
            }
        }
    }

    private fun DatabaseReference.updateUser(uid:String, updatesMap: Map<String,Any>, onSuccessListener: () -> Unit){
        child("users").child(mAuth.currentUser!!.uid).updateChildren(updatesMap).addOnCompleteListener {
            if(it.isSuccessful){
                onSuccessListener()
            }else{
                showToast(it.exception!!.message!!)
            }
        }
    }

    private fun FirebaseUser.reauthenticate(credential: AuthCredential, onSuccessListener: () ->  Unit) {
        reauthenticate(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccessListener()
            } else {
                //showToast(it.exception!!.message!!)
                showToast("You entered wrong password")
                Log.d(TAG, "onPasswordConfirm: failure faze 2")
            }
        }
    }

}



