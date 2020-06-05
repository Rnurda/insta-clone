package com.ryspay.nurda.screens.addfriends

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.ryspay.nurda.screens.data.FeedPostsRepository
import com.ryspay.nurda.screens.data.UsersRepository
import com.ryspay.nurda.screens.map
import com.ryspay.nurda.models.User

class AddFriendsViewModel (private val onFailureListener: OnFailureListener, private val usersRepository: UsersRepository, private val feedPostsRepository: FeedPostsRepository): ViewModel(){
    val userAndFriends: LiveData<Pair<User, List<User>>> =
        usersRepository.getUsers().map{ allUsers ->
        val(userList, otherUsersList) = allUsers.partition {
            it.uid == usersRepository.currentUid()
        }
        userList.first() to otherUsersList
    }
    fun setFollow(currentUid: String, uid: String, follow: Boolean): Task<Void> {
        return (if(follow){
            Tasks.whenAll(
                usersRepository.addFollow(currentUid, uid),
                usersRepository.addFollower(currentUid,uid),
                feedPostsRepository.copyFeedPosts(postsAuthorUid = uid, uid = currentUid))
        }else{
            Tasks.whenAll(
                usersRepository.deleteFollow(currentUid, uid),
                usersRepository.deleteFollower(currentUid,uid),
                feedPostsRepository.deleteFeedPosts(postAuthorUid = uid, uid = currentUid)
            )
        }).addOnFailureListener(onFailureListener)
    }
}