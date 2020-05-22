package com.ryspay.nurda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ryspay.nurda.models.User
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : AppCompatActivity() {
    private val TAG = "EditProfileActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        Log.d(TAG, "onCreate: ");

        close_image.setOnClickListener {
            finish()
            overridePendingTransition(0,0)
        }

        val mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        val mDatabase = FirebaseDatabase.getInstance().reference
        mDatabase.child("users").child(user!!.uid).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(data: DataSnapshot) {
                val user = data.getValue(User::class.java)
                name_input.setText(user!!.name, TextView.BufferType.EDITABLE)
                username_input.setText(user.username, TextView.BufferType.EDITABLE)
                web_input.setText(user.website, TextView.BufferType.EDITABLE)
                bio_input.setText(user.bio, TextView.BufferType.EDITABLE)
                email_input.setText(user.email, TextView.BufferType.EDITABLE)
                number_input.setText(user.phone, TextView.BufferType.EDITABLE)
                gender_input.setText(user.gender, TextView.BufferType.EDITABLE)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "onDataChange: ",error.toException())
            }
        })



    }
}

