package com.ryspay.nurda.activities

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
import com.google.firebase.auth.FirebaseAuth
import com.ryspay.nurda.R
import com.ryspay.nurda.utils.FirebaseHelper
import com.ryspay.nurda.utils.GlideApp
import com.ryspay.nurda.utils.ValueEventListenerAdapter
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.feed_item.view.*

class HomeActivity : BaseActivity(0) {
    private val TAG = "HomeActivity"
    private lateinit var mFirebase: FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setUpBottomNavigation()
        Log.d(TAG, "onCreate: ")
        mFirebase = FirebaseHelper(this)
        mFirebase.auth.addAuthStateListener {
            if(it.currentUser == null){
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }else{

            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = mFirebase.auth.currentUser
        if(currentUser == null){
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }else{
            mFirebase.database.child("feed-posts").child(currentUser.uid)
                .addValueEventListener(ValueEventListenerAdapter{
                    val posts = it.children.map { it.getValue(FeedPost::class.java)!! }
                    /*Log.d(TAG, "feedPosts: ${posts.joinToString("\n", "\n")} ")
                    Log.d(TAG, "feedPosts: ${posts.first().timeStampsDate()} ")*/

                    feed_recycler.adapter = FeedAdapter(posts)
                    feed_recycler.layoutManager = LinearLayoutManager(this)
                })
        }
    }
}
class FeedAdapter(private val posts: List<FeedPost>) : RecyclerView.Adapter<FeedAdapter.ViewHolder>(){
    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.feed_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
        val post = posts[position]
        with(holder) {
            view.user_photo_image.loadImage(post.photo)
            view.username_text.text = post.username
            view.post_image.loadImage(post.image)
            if(post.likesCount == 0){
                view.likes_text.visibility = View.GONE
            }else{
                view.likes_text.visibility = View.VISIBLE
                view.likes_text.text = "${post.likesCount} likes"
            }
            view.caption_text.setCaptionText(post.username, post.caption)
        }
    }

    fun TextView.setCaptionText(username: String, caption: String){
        val usernameSpanable = SpannableString(username)
        usernameSpanable.setSpan(StyleSpan(Typeface.BOLD),
            0, usernameSpanable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        usernameSpanable.setSpan(object : ClickableSpan() {
            override fun onClick(view: View) {
                view.context.showToast("Username is clicked")
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
