package com.ryspay.nurda.activities.addfriends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class AddFriendsViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AddFriendsViewModel(FirebaseAddFriendsRepository()) as T
    }
}