package com.ryspay.nurda.screens

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.ryspay.nurda.R
import com.ryspay.nurda.models.FeedPost
import com.ryspay.nurda.models.User
import com.ryspay.nurda.utils.CameraHelper
import com.ryspay.nurda.utils.FirebaseHelper
import com.ryspay.nurda.utils.GlideApp
import com.ryspay.nurda.utils.ValueEventListenerAdapter
import com.ryspay.nurda.views.setUpBottomNavigation
import kotlinx.android.synthetic.main.activity_share.*

class ShareActivity : BaseActivity() {
    private val TAG = "ShareActivity"
    private lateinit var mCamera: CameraHelper
    private lateinit var mFirebase: FirebaseHelper
    private lateinit var mUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        Log.d(TAG, "onCreate: ")

        mFirebase = FirebaseHelper(this)
        mCamera = CameraHelper(this)
        mCamera.takeCameraPicture()

        mFirebase.currentUserReference().addValueEventListener(ValueEventListenerAdapter{
            mUser = it.asUser()!!
        })

        back_image.setOnClickListener{finish()}
        share_text.setOnClickListener { share() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == mCamera.REQUEST_CODE ){
            if(resultCode == Activity.RESULT_OK) {
                GlideApp.with(this).load(mCamera.imageUri).centerCrop().into(post_image)
            }else{
                finish()
            }
        }
    }

    private fun share() {
        val imageUri = mCamera.imageUri
        val uid = mFirebase.currentUid()!!
        if(imageUri!=null) {
            // upload image to user folder <- Storage
            mFirebase.storage.child("users").child(uid).child("images")
                .child(imageUri.lastPathSegment).putFile(imageUri)
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        // add image to user images <- DB
                        val downloadTask = it.result!!.metadata!!.reference!!.downloadUrl
                        downloadTask.addOnSuccessListener { downloadUrl ->
                            val imageDownloadURL = downloadUrl.toString()
                            mFirebase.database.child("images").child(uid).push().setValue(imageDownloadURL)
                                .addOnCompleteListener {
                                    if(it.isSuccessful){
                                        mFirebase.database.child("feed-posts").child(uid)
                                            .push()
                                            .setValue(mkFeedPost(uid, imageDownloadURL))
                                            .addOnCompleteListener {
                                                if(it.isSuccessful){
                                                    startActivity(Intent(this, ProfileActivity::class.java))
                                                    finish()
                                                }
                                            }
                                    }else{
                                        showToast(it.exception!!.message!!)
                                    }
                                }
                        }
                    }else{
                        showToast(it.exception!!.message!!)
                    }
                }
        }
    }

    private fun mkFeedPost(
        uid: String,
        imageDownloadURL: String
    ): FeedPost {
        return FeedPost(
            uid = uid,
            username = mUser.username,
            image = imageDownloadURL,
            caption = caption_input.text.toString(),
            photo = mUser.photo
        )
    }
}

