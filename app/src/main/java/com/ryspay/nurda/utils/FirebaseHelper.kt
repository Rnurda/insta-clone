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
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    val storage: StorageReference = FirebaseStorage.getInstance().reference
    val uid = auth.currentUser!!.uid

    fun uploadUserPhoto(photo: Uri, onSuccessListener: (UploadTask.TaskSnapshot) -> Unit) {
        storage.child("users/$uid/photo").putFile(photo).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccessListener(it.result!!)
            } else {
                activity.showToast(it.exception!!.message!!)
            }
        }
    }

    fun updateUserPhoto(photoUrl: String, onSuccessListener: () -> Unit){
        database.child("users/$uid/photo").setValue(photoUrl)
            .addOnCompleteListener {task ->
                if(task.isSuccessful){
                    onSuccessListener()
                }else{
                    activity.showToast(task.exception!!.message!!)
                }
            }
    }

    fun updateEmail(email: String, onSuccessListener: () ->  Unit){
        auth.currentUser!!.updateEmail(email).addOnCompleteListener {
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
        database.child("users").child(uid).updateChildren(updatesMap).addOnCompleteListener {
            if(it.isSuccessful){
                onSuccessListener()
            }else{
                activity.showToast(it.exception!!.message!!)
            }
        }
    }

    fun reauthenticate(credential: AuthCredential, onSuccessListener: () ->  Unit) {
        auth.currentUser!!.reauthenticate(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccessListener()
            } else {
                //activity.showToast(it.exception!!.message!!)
                activity.showToast("You entered wrong password")
                //Log.d(TAG, "onPasswordConfirm: failure faze 2")
            }
        }
    }

    fun currentUserReference(): DatabaseReference =  database.child("users").child(uid)
}