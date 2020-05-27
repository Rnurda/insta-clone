package com.ryspay.nurda.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.ServerValue
import java.util.*

data class FeedPost(val uid: String = "", val username: String = "", val image: String = "", val caption: String = "",
                    val comments: List<Comment> = emptyList(), val timeStamp: Any = ServerValue.TIMESTAMP,
                    val photo: String? =null,
                    @Exclude val id: String = "", @Exclude val commentsCount: Int = 0){
    fun timeStampsDate(): Date =
        Date(timeStamp as Long)
}
