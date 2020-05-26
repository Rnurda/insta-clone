package com.ryspay.nurda.utils

import android.app.Activity
import android.net.Uri
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.ryspay.nurda.activities.showToast

class FirebaseHelper(private val activity: Activity){
    private val mAuth: FirebaseAuth =
        FirebaseAuth.getInstance()
    private val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val mStorage: StorageReference = FirebaseStorage.getInstance().reference

    private val uid = mAuth.currentUser!!.uid

    fun upleadUserPhoto(photo: Uri, onSuccessListener: (UploadTask.TaskSnapshot) -> Unit) {
        mStorage.child("users/$uid/photo").putFile(photo).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccessListener(it.result!!)
            } else {
                activity.showToast(it.exception!!.message!!)
            }
        }
    }

    fun updateUserPhoto(photoUrl: String, onSuccessListener: () -> Unit){
        mDatabase.child("users/$uid/photo").setValue(photoUrl)
            .addOnCompleteListener {task ->
                if(task.isSuccessful){
                    onSuccessListener()
                }else{
                    activity.showToast(task.exception!!.message!!)
                }
            }
    }

    fun updateEmail(email: String, onSuccessListener: () ->  Unit){
        mAuth.currentUser!!.updateEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccessListener()
            } else {
                //activity.showToast(it.exception!!.message!!)
                activity.showToast("You enter wrong password")
                //Log.d(TAG, "onPasswordConfirm: failure faze 2")
            }
        }
    }

    fun updateUser(updatesMap: Map<String,Any?>, onSuccessListener: () -> Unit){
        mDatabase.child("users").child(uid).updateChildren(updatesMap).addOnCompleteListener {
            if(it.isSuccessful){
                onSuccessListener()
            }else{
                activity.showToast(it.exception!!.message!!)
            }
        }
    }

    fun reauthenticate(credential: AuthCredential, onSuccessListener: () ->  Unit) {
        mAuth.currentUser!!.reauthenticate(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccessListener()
            } else {
                //activity.showToast(it.exception!!.message!!)
                activity.showToast("You entered wrong password")
                //Log.d(TAG, "onPasswordConfirm: failure faze 2")
            }
        }
    }

    fun currentUserReference(): DatabaseReference =  mDatabase.child("users").child(uid)
}