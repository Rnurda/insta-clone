package com.ryspay.nurda.screens.addfriends

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ryspay.nurda.R
import com.ryspay.nurda.screens.common.loadUserPhoto
import com.ryspay.nurda.models.User
import kotlinx.android.synthetic.main.add_fiends_item.view.*

class FriendsAdapter(private val listener: Listener) : RecyclerView.Adapter<FriendsAdapter.ViewHolder>(){

    class ViewHolder(val view: View): RecyclerView.ViewHolder(view)

    interface Listener{
        fun follow(uid: String)
        fun unfollow(uid: String)
    }

    private var mPosition = mapOf<String, Int>()
    private var mFollows = mapOf<String,Boolean>()
    private var mUsers = listOf<User>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.add_fiends_item, parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            val user = mUsers[position]
            view.photo_image.loadUserPhoto(user.photo)
            view.username_text.text = user.username
            view.name_text.text = user.name
            view.follow_btn.setOnClickListener{listener.follow(user.uid)}
            view.unfollow_btn.setOnClickListener{listener.unfollow(user.uid)}

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
        mPosition = users.withIndex().map { (idx, user) ->user.uid to idx }.toMap()
        mFollows = follows
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int  = mUsers.size

    fun followed(uid: String) {
        mFollows = mFollows + (uid to true)
        notifyItemChanged(mPosition[uid]!!)
    }

    fun unfollowed(uid: String) {
        mFollows = mFollows - uid
        notifyItemChanged(mPosition[uid]!!)
    }
}