package com.ryspay.nurda.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ryspay.nurda.screens.addfriends.AddFriendsViewModel
import com.ryspay.nurda.screens.data.firebase.FirebaseFeedPostsRepository
import com.ryspay.nurda.screens.editprofile.EditProfielViewModel
import com.ryspay.nurda.screens.data.firebase.FirebaseUsersRepository

@Suppress("UNCHECKED_CAST")
class ViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AddFriendsViewModel::class.java)) {
            return AddFriendsViewModel(FirebaseUsersRepository(),FirebaseFeedPostsRepository()) as T
        }else if(modelClass.isAssignableFrom(EditProfielViewModel::class.java)){
            return EditProfielViewModel(FirebaseUsersRepository()) as T
        }
        else{
            error("Unknown viewModel class $modelClass")
        }
    }
}