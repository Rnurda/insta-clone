package com.ryspay.nurda.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ValueEventListener
import com.ryspay.nurda.R
import com.ryspay.nurda.models.FeedPost
import com.ryspay.nurda.utils.FirebaseHelper
import com.ryspay.nurda.utils.GlideApp
import com.ryspay.nurda.utils.ValueEventListenerAdapter
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.feed_item.view.*

class HomeActivity : BaseActivity(0), FeedAdapter.Listener {
    private lateinit var mAdapter: FeedAdapter
    private val TAG = "HomeActivity"
    private lateinit var mFirebase: FirebaseHelper
    private var mLikesListener: Map<String, ValueEventListener> = emptyMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setUpBottomNavigation()
        Log.d(TAG, "onCreate: ")
        mFirebase = FirebaseHelper(this)
        mFirebase.auth.addAuthStateListener {
            if (it.currentUser == null) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {

            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = mFirebase.auth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            mFirebase.database.child("feed-posts").child(currentUser.uid)
                .addValueEventListener(ValueEventListenerAdapter {
                    val posts = it.children.map { it.asFeedPost()!! }
                        .sortedByDescending { it.timeStampsDate() }

                    mAdapter = FeedAdapter(this, posts)
                    feed_recycler.adapter = mAdapter
                    feed_recycler.layoutManager = LinearLayoutManager(this)
                })
        }
    }

    override fun toggleLike(postId: String) {
        Log.d(TAG, "toggleLike: ${postId}")
        val reference = mFirebase.database.child("likes").child(postId).child(mFirebase.currentUid()!!)
        reference.addListenerForSingleValueEvent(ValueEventListenerAdapter {
            reference.setValueTrueOrRemove(!it.exists())
        })
    }

    override fun loadLikes(postId: String, position: Int) {
        fun createListener() =
            mFirebase.database.child("likes").child(postId).addValueEventListener(ValueEventListenerAdapter{
                val userLikes = it.children.map { it.key }.toSet() //set to optimize searching
                val postsLikes = FeedPostsLikes(userLikes.size, userLikes.contains(mFirebase.currentUid()))
                mAdapter.upadatePostLikes(position, postsLikes)
            })
        val createNewListener = mLikesListener[postId] == null
        Log.d(TAG, "loadLike: $position ------------  $createNewListener")
        if(createNewListener){
            mLikesListener += (postId to createListener())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mLikesListener.values.forEach {
            mFirebase.database.removeEventListener(it)
        }
    }
}
data class FeedPostsLikes(val likesCount: Int, val likedByUser: Boolean){

}

class FeedAdapter(private val listener: Listener, private val posts: List<FeedPost>) : RecyclerView.Adapter<FeedAdapter.ViewHolder>(){
    interface Listener{
        fun toggleLike(postId: String)
        fun loadLikes(postId: String, position: Int)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    private var postsLikes: Map<Int, FeedPostsLikes> = emptyMap()
    private val defaultPostLikes = FeedPostsLikes(0, false)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.feed_item, parent, false)
        return ViewHolder(view)
    }

    fun upadatePostLikes(position: Int, likes: FeedPostsLikes) {
        postsLikes += (position to likes)
        notifyItemChanged(position)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
        val post = posts[position]
        val likes = postsLikes[position] ?: defaultPostLikes
        with(holder.view) {
            user_photo_image.loadUserPhoto(post.photo)
            username_text.text = post.username
            post_image.loadImage(post.image)
            if(likes.likesCount == 0){
                likes_text.visibility = View.GONE
            }else{
                likes_text.visibility = View.VISIBLE
                val likes_with_quantity_string = holder.view.context.resources.getQuantityString(R.plurals.likes_count, likes.likesCount)
                likes_text.text = "${likes.likesCount} $likes_with_quantity_string"
            }
            caption_text.setCaptionText(post.username, post.caption)
            like_image.setOnClickListener { listener.toggleLike(post.id)}
            like_image.setImageResource(if(likes.likedByUser) R.drawable.ic_like_red else R.drawable.ic_likes_border)
            listener.loadLikes(post.id, position)
        }
    }

    fun TextView.setCaptionText(username: String, caption: String){
        val usernameSpanable = SpannableString(username)
        usernameSpanable.setSpan(StyleSpan(Typeface.BOLD),
            0, usernameSpanable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        usernameSpanable.setSpan(object : ClickableSpan() {
            override fun onClick(view: View) {
                view.context.showToast(context.getString(R.string.username_is_clicked))
            }

            override fun updateDrawState(ds: TextPaint) {}
        }, 0, usernameSpanable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        caption_text.text = SpannableStringBuilder()
            .append(usernameSpanable).append(" ").append(caption)
        caption_text.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun getItemCount(): Int  = posts.size

    private fun ImageView.loadImage(image: String?){
        GlideApp.with(this).load(image).centerCrop().into(this)
    }
}
