package com.ryspay.nurda.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
        holder.view.post_image.loadImage(posts[position].image)
    }

    override fun getItemCount(): Int  = posts.size

    private fun ImageView.loadImage(image: String){
        GlideApp.with(this).load(image).centerCrop().into(this)
    }
}
