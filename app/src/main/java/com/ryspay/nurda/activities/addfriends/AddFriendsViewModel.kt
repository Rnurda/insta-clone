package com.ryspay.nurda.activities.addfriends

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.ryspay.nurda.activities.asUser
import com.ryspay.nurda.activities.map
import com.ryspay.nurda.activities.setValueTrueOrRemove
import com.ryspay.nurda.activities.task
import com.ryspay.nurda.models.User
import com.ryspay.nurda.utils.FirebaseLiveData
import com.ryspay.nurda.utils.TaskSourceOnCompleteListener
import com.ryspay.nurda.utils.ValueEventListenerAdapter

class AddFriendsViewModel (private val repository: AddFriendsRepository): ViewModel(){
    val userAndFriends: LiveData<Pair<User, List<User>>> =
        repository.getUsers().map{allUsers ->
        val(userList, otherUsersList) = allUsers.partition {
            it.uid == repository.currentUid()
        }
        userList.first() to otherUsersList
    }
    fun setFollow(currentUid: String, uid: String, follow: Boolean): Task<Void> {
        return if(follow){
            Tasks.whenAll(
                repository.addFollow(currentUid, uid),
                repository.addFollower(currentUid,uid),
                repository.copyFeedPosts(postsAuthorUid = uid, uid = currentUid))
        }else{
            Tasks.whenAll(
                repository.deleteFollow(currentUid, uid),
                repository.deleteFollower(currentUid,uid),
                repository.deleteFeedPosts(postAuthorUid = uid, uid = currentUid)
            )
        }
    }
}