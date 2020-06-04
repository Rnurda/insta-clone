package com.ryspay.nurda.screens.data.firebase

import android.net.Uri
import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.ryspay.nurda.screens.asUser
import com.ryspay.nurda.screens.data.UsersRepository
import com.ryspay.nurda.screens.map
import com.ryspay.nurda.models.User
import com.ryspay.nurda.utils.*

class FirebaseUsersRepository: UsersRepository {
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

        override fun currentUid() = FirebaseAuth.getInstance().currentUser?.uid

        override fun getUsers(): LiveData<List<User>> =
            database.child("users").liveData().map {
                it.children.map { it.asUser()!! }
            }

        override fun addFollow(fromUid: String, toUid: String): Task<Unit> =
            getFollowRef(fromUid, toUid).setValue(true).toUnit()

        override fun deleteFollow(fromUid: String, toUid: String): Task<Unit> =
            getFollowRef(fromUid, toUid).removeValue().toUnit()

        override fun addFollower(fromUid: String, toUid: String): Task<Unit> =
            getFollowersRef(fromUid, toUid).setValue(true).toUnit()

        override fun deleteFollower(fromUid: String, toUid: String): Task<Unit> =
            getFollowersRef(fromUid, toUid).removeValue().toUnit()

        private fun getFollowRef(fromUid: String, toUid: String) =
            database.child("users").child(fromUid).child("follows").child(toUid)

        private fun getFollowersRef(fromUid: String, toUid: String) =
            database.child("users").child(toUid).child("followers").child(fromUid)

    }