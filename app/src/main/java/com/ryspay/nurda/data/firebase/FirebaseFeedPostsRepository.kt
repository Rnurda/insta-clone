package com.ryspay.nurda.data.firebase

import com.google.android.gms.tasks.Task
import com.ryspay.nurda.common.TaskSourceOnCompleteListener
import com.ryspay.nurda.common.ValueEventListenerAdapter
import com.ryspay.nurda.data.FeedPostsRepository
import com.ryspay.nurda.common.task
import com.ryspay.nurda.common.toUnit
import com.ryspay.nurda.data.firebase.common.database

class FirebaseFeedPostsRepository :
    FeedPostsRepository {

    override fun copyFeedPosts(postsAuthorUid: String, uid: String): Task<Unit> =
        task { taskSource ->
            database.child("feed-posts")
                .child(postsAuthorUid)
                .orderByChild("uid")
                .equalTo(postsAuthorUid)
                .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                    val postsMap = it.children.map { it.key to it.value }.toMap()
                    database.child("feed-posts")
                        .child(uid).updateChildren(postsMap)
                        .toUnit()
                        .addOnCompleteListener(
                            TaskSourceOnCompleteListener(
                                taskSource
                            )
                        )
                })
        }

    override fun deleteFeedPosts(postAuthorUid: String, uid: String): Task<Unit> =
        task { taskSource ->
            database.child("feed-posts").child(uid)
                .orderByChild("uid")
                .equalTo(postAuthorUid)
                .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                    val postsMap = it.children.map { it.key to null }.toMap()
                    database.child("feed-posts")
                        .child(uid).updateChildren(postsMap)
                        .toUnit()
                        .addOnCompleteListener(
                            TaskSourceOnCompleteListener(
                                taskSource
                            )
                        )
                })
        }

}