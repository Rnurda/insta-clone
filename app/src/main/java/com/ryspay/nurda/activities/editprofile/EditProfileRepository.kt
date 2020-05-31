package com.ryspay.nurda.activities.editprofile

import android.net.Uri
import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.EmailAuthProvider
import com.ryspay.nurda.R
import com.ryspay.nurda.activities.addfriends.toUnit
import com.ryspay.nurda.activities.asUser
import com.ryspay.nurda.activities.map
import com.ryspay.nurda.activities.showToast
import com.ryspay.nurda.models.User
import com.ryspay.nurda.utils.*

interface EditProfileRepository {
    fun getUser(): LiveData<User>
    fun uploadUserPhoto(localImage: Uri): Task<Uri>
    fun updateUserPhoto(downloadUlr: Uri): Task<Unit>
    fun updateEmail(currentEmail: String, newEmail: String, password: String): Task<Unit>
    fun updateUserProfile(currentUser: User, newUser: User): Task<Unit>
}

class FirebaseEditProfileRepository: EditProfileRepository{
    override fun getUser(): LiveData<User> =
        database.child("users").child(currentUid()!!).liveData().map {
            it.asUser()!!
        }

    override fun uploadUserPhoto(localImage: Uri): Task<Uri> =
        storage.child("users/${currentUid()!!}/photo").putFile(localImage).onSuccessTask {
            it?.metadata!!.reference!!.downloadUrl.addOnSuccessListener {uri ->
                val photoUrl = uri.toString()
                Tasks.forResult(photoUrl)
            }
        }

    override fun updateUserPhoto(downloadUrl: Uri): Task<Unit> =
        database.child("users/${currentUid()!!}/photo").setValue(downloadUrl.toString()).toUnit()

    override fun updateEmail(currentEmail: String, newEmail: String, password: String): Task<Unit> {
        val currentUser = auth.currentUser
        return if(currentUser != null){
            val credential = EmailAuthProvider.getCredential(currentEmail, password)
           currentUser.reauthenticate(credential).onSuccessTask{
                // update email in auth
               currentUser.updateEmail(newEmail)
            }.toUnit() }else{
                Tasks.forException(IllegalAccessException("User not authenticated!"))
            }
    }

    override fun updateUserProfile(currentUser: User, newUser: User): Task<Unit> {
        val updatesMap = mutableMapOf<String, Any?>()
        if(newUser.bio != currentUser.bio) updatesMap["bio"] = newUser.bio
        if(newUser.email != currentUser.email) updatesMap["email"] = newUser.email
        if(newUser.gender != currentUser.gender) updatesMap["gender"] = newUser.gender
        if(newUser.name != currentUser.name) updatesMap["name"] = newUser.name
        if(newUser.phone != currentUser.phone) updatesMap["phone"] = newUser.phone
        if(newUser.username != currentUser.username) updatesMap["username"] = newUser.username
        if(newUser.website != currentUser.website) updatesMap["website"] = newUser.website

        return database.child("users").child(currentUid()!!).updateChildren(updatesMap).toUnit()
    }


}