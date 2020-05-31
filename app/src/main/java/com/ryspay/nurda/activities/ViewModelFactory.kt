package com.ryspay.nurda.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ryspay.nurda.activities.addfriends.AddFriendsViewModel
import com.ryspay.nurda.activities.addfriends.FirebaseAddFriendsRepository
import com.ryspay.nurda.activities.editprofile.EditProfielViewModel
import com.ryspay.nurda.activities.editprofile.FirebaseEditProfileRepository

@Suppress("UNCHECKED_CAST")
class ViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AddFriendsViewModel::class.java)) {
            return AddFriendsViewModel(FirebaseAddFriendsRepository()) as T
        }else if(modelClass.isAssignableFrom(EditProfielViewModel::class.java)){
            return EditProfielViewModel(FirebaseEditProfileRepository()) as T
        }
        else{
            error("Unknown viewModel class $modelClass")
        }
    }
}