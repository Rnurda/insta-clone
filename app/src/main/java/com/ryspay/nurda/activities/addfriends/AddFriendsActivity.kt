package com.ryspay.nurda.activities.addfriends

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ryspay.nurda.R
import com.ryspay.nurda.activities.ViewModelFactory
import com.ryspay.nurda.activities.showToast
import com.ryspay.nurda.models.User
import kotlinx.android.synthetic.main.activity_add_friends.*

class  AddFriendsActivity : AppCompatActivity(), FriendsAdapter.Listener {
    private lateinit var mViewModel: AddFriendsViewModel
    private lateinit var mAdapter: FriendsAdapter
    private lateinit var mUsers: List<User>
    private lateinit var mUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friends)
        back_image.setOnClickListener { finish() }

        mAdapter = FriendsAdapter(this)
        mViewModel = ViewModelProvider(this,
            ViewModelFactory()
        )
            .get(AddFriendsViewModel::class.java)

        add_friends_recycler.adapter = mAdapter
        add_friends_recycler.layoutManager = LinearLayoutManager(this)

        mViewModel.userAndFriends.observe(this, Observer {
                it?.let { (user, otherUsers) ->
                    mUser = user
                    mUsers = otherUsers

                    mAdapter.update(mUsers, mUser.follows)
                }
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
        mViewModel.setFollow(mUser.uid, uid, follow)
            .addOnSuccessListener { onSuccessListener() }
            .addOnFailureListener {showToast(it.message)}
    }
}

