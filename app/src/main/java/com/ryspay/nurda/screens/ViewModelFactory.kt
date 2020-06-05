package com.ryspay.nurda.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnFailureListener
import com.ryspay.nurda.screens.addfriends.AddFriendsViewModel
import com.ryspay.nurda.screens.data.firebase.FirebaseFeedPostsRepository
import com.ryspay.nurda.screens.editprofile.EditProfielViewModel
import com.ryspay.nurda.screens.data.firebase.FirebaseUsersRepository

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val onFailureListener: OnFailureListener): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AddFriendsViewModel::class.java)) {
            return AddFriendsViewModel(onFailureListener, FirebaseUsersRepository(),FirebaseFeedPostsRepository()) as T
        }else if(modelClass.isAssignableFrom(EditProfielViewModel::class.java)){
            return EditProfielViewModel(onFailureListener, FirebaseUsersRepository()) as T
        }
        else{
            error("Unknown viewModel class $modelClass")
        }
    }
}