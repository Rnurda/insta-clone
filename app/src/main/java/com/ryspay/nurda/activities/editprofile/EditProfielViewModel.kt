package com.ryspay.nurda.activities.editprofile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.UploadTask
import com.ryspay.nurda.activities.addfriends.FirebaseAddFriendsRepository
import com.ryspay.nurda.activities.showToast
import com.ryspay.nurda.models.User

class EditProfielViewModel(private val repository: EditProfileRepository) : ViewModel() {
    val user: LiveData<User> = repository.getUser()


    fun uploadAndSetUserPhoto(localImage: Uri): Task<Unit> =
        repository.uploadUserPhoto(localImage).onSuccessTask{downloadUlr ->
            repository.updateUserPhoto(downloadUlr!!)
        }

    fun updateEmail(currentEmail: String, newEmail: String, password: String): Task<Unit> =
        repository.updateEmail(currentEmail = currentEmail, newEmail = newEmail, password = password )

    fun updateUserProfile(currentUser: User, newUser: User): Task<Unit> =
        repository.updateUserProfile(currentUser = currentUser, newUser = newUser)

}