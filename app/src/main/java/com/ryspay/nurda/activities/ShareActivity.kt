package com.ryspay.nurda.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.ryspay.nurda.R
import com.ryspay.nurda.utils.CameraHelper
import com.ryspay.nurda.utils.FirebaseHelper
import com.ryspay.nurda.utils.GlideApp
import kotlinx.android.synthetic.main.activity_share.*

class ShareActivity : BaseActivity(2) {
    private val TAG = "ShareActivity"
    private lateinit var mCamera: CameraHelper
    private lateinit var mFirebase: FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        Log.d(TAG, "onCreate: ")

        mFirebase = FirebaseHelper(this)
        mCamera = CameraHelper(this)
        mCamera.takeCameraPicture()

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
        val uid = mFirebase.uid
        if(imageUri!=null) {
            // upload image to user folder <- Storage
            mFirebase.storage.child("users").child(uid).child("images")
                .child(imageUri.lastPathSegment).putFile(imageUri)
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        // add image to user images <- DB
                        val downloadTask = it.result!!.metadata!!.reference!!.downloadUrl
                        downloadTask.addOnSuccessListener { downloadUrl ->
                            mFirebase.database.child("images").child(uid).push().setValue(downloadUrl.toString())
                                .addOnCompleteListener {
                                    if(it.isSuccessful){
                                        startActivity(Intent(this, ProfileActivity::class.java))
                                        finish()
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


}

