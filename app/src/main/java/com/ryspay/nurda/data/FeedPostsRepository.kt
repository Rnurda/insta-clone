package com.ryspay.nurda.data

import com.google.android.gms.tasks.Task

interface FeedPostsRepository {
    fun copyFeedPosts(postsAuthorUid: String, uid: String): Task<Unit>
    fun deleteFeedPosts(postAuthorUid: String, uid: String): Task<Unit>
}