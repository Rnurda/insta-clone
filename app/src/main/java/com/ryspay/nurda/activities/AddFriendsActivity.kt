package com.ryspay.nurda.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnSuccessListener
import com.ryspay.nurda.R
import com.ryspay.nurda.models.User
import com.ryspay.nurda.utils.FirebaseHelper
import com.ryspay.nurda.utils.ValueEventListenerAdapter
import kotlinx.android.synthetic.main.activity_add_friends.*
import kotlinx.android.synthetic.main.add_fiends_item.view.*

class  AddFriendsActivity : AppCompatActivity(), FriendsAdapter.Listener {

    private lateinit var mAdapter: FriendsAdapter
    private lateinit var mUsers: List<User>
    private lateinit var mUser: User
    private lateinit var mFirebase: FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friends)

        mFirebase = FirebaseHelper(this)
        mAdapter = FriendsAdapter(this)

        val uid = mFirebase.auth.currentUser!!.uid
        add_friends_recycler.adapter = mAdapter
        add_friends_recycler.layoutManager = LinearLayoutManager(this)
        back_image.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }
        mFirebase.database.child("users").addValueEventListener(ValueEventListenerAdapter{
            val allUsers = it.children.map { it.getValue(User::class.java)!!.copy(uid = it.key)}
            val(userList, otherUsersList) = allUsers.partition { it.uid == uid }
            mUser = userList.first()
            mUsers = otherUsersList
            mAdapter.update(mUsers, mUser.follows)
        })
    }

    override fun follow(uid: String) {
        setFollow(uid, true){
            mAdapter.followed(uid)
        }
    }

    override fun unfollow(uid: String) {
        setFollow(uid, false){
            mAdapter.unfollowed(uid)
        }
    }

    private fun setFollow(uid: String, follow: Boolean, onSuccessListener: () -> Unit){
        val followTask = mFirebase.database.child("users").child(mUser.uid!!).child("follows").child(uid)
        val setFollow = if(follow) followTask.setValue(true) else followTask.removeValue()
        val followerTask = mFirebase.database.child("users").child(uid).child("followers").child(mUser.uid!!)
        val setFollower = if(follow) followerTask.setValue(true) else followerTask.removeValue()

        setFollow.continueWithTask({setFollower}).addOnCompleteListener {
            if(it.isSuccessful){
                onSuccessListener()
            }else{
                showToast(it.exception!!.message!!)
            }
        }
    }
}

class FriendsAdapter(private val listener: Listener) : RecyclerView.Adapter<FriendsAdapter.ViewHolder>(){

    private var mPosition = mapOf<String, Int>()
    private var mFollows = mapOf<String,Boolean>()
    private var mUsers = listOf<User>()

    class ViewHolder(val view: View): RecyclerView.ViewHolder(view)

    interface Listener{
        fun follow(uid: String)
        fun unfollow(uid: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.add_fiends_item, parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            val user = mUsers[position]
            view.photo_image.loadImage(user.photo)
            view.username_text.text = user.username
            view.name_text.text = user.name
            view.follow_btn.setOnClickListener{listener.follow(user.uid!!)}
            view.unfollow_btn.setOnClickListener{listener.unfollow(user.uid!!)}

            val follows = mFollows[user.uid] ?: false
            if(follows){
                view.follow_btn.visibility = View.GONE
                view.unfollow_btn.visibility = View.VISIBLE
            }else{
                view.unfollow_btn.visibility = View.GONE
                view.follow_btn.visibility = View.VISIBLE
            }
         }
    }

    fun update(users: List<User>, follows: Map<String, Boolean>){
        mUsers = users
        mPosition = users.withIndex().map { (idx, user) ->user.uid!! to idx }.toMap()
        mFollows = follows
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int  = mUsers.size

    fun followed(uid: String) {
        mFollows += (uid to true)
        notifyItemChanged(mPosition[uid]!!)
    }

    fun unfollowed(uid: String) {
        mFollows -= uid
        notifyItemChanged(mPosition[uid]!!)
    }
}
