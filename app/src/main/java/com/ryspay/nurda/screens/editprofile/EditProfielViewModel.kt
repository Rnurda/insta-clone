package com.ryspay.nurda.screens.editprofile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.ryspay.nurda.screens.data.UsersRepository
import com.ryspay.nurda.models.User

class EditProfielViewModel(private val usersRepository: UsersRepository) : ViewModel() {
    val user: LiveData<User> = usersRepository.getUser()


    fun uploadAndSetUserPhoto(localImage: Uri): Task<Unit> =
        usersRepository.uploadUserPhoto(localImage).onSuccessTask{ downloadUlr ->
            usersRepository.updateUserPhoto(downloadUlr!!)
        }

    fun updateEmail(currentEmail: String, newEmail: String, password: String): Task<Unit> =
        usersRepository.updateEmail(currentEmail = currentEmail, newEmail = newEmail, password = password )

    fun updateUserProfile(currentUser: User, newUser: User): Task<Unit> =
        usersRepository.updateUserProfile(currentUser = currentUser, newUser = newUser)

}