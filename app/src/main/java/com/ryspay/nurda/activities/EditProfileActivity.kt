package com.ryspay.nurda.activities

import PasswordDialog
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.EmailAuthProvider
import com.ryspay.nurda.R
import com.ryspay.nurda.models.User
import com.ryspay.nurda.utils.CameraHelper
import com.ryspay.nurda.utils.FirebaseHelper
import com.ryspay.nurda.utils.ValueEventListenerAdapter
import kotlinx.android.synthetic.main.activity_edit_profile.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class EditProfileActivity : AppCompatActivity(), View.OnClickListener, PasswordDialog.Listener {
    private val TAG = "EditProfileActivity"
    private lateinit var mFirebaseHelper: FirebaseHelper
    private lateinit var mUser: User
    private lateinit var mPendingUser: User
    private lateinit var cameraPictureTaker: CameraHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        Log.d(TAG, "onCreate: ");

        cameraPictureTaker = CameraHelper(this)
        mFirebaseHelper = FirebaseHelper(this)

        close_image.setOnClickListener (this)
        save_image.setOnClickListener(this)
        chnage_photo_lable.setOnClickListener(this)

        mFirebaseHelper.currentUserReference()
            .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                mUser = it.asUser()!!
                name_input.setText(mUser.name)
                username_input.setText(mUser.username)
                web_input.setText(mUser.website)
                bio_input.setText(mUser.bio)
                email_input.setText(mUser.email)
                number_input.setText(mUser.phone)
                gender_input.setText(mUser.gender)
                profile_image.loadUserPhoto(mUser.photo)
            })
    }
    override fun onClick(view: View) {
        when(view.id){
            R.id.save_image ->{
                updateProfile()
            }
            R.id.close_image ->{
                finish()
                overridePendingTransition(0,0)
            }
            R.id.chnage_photo_lable ->{
                cameraPictureTaker.takeCameraPicture()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == cameraPictureTaker.REQUEST_CODE && resultCode == Activity.RESULT_OK){
            // upload image to FirebaseStorage
            mFirebaseHelper.uploadUserPhoto(cameraPictureTaker.imageUri!!){
                val downloadTask = it.metadata!!.reference!!.downloadUrl
                downloadTask.addOnSuccessListener {uri ->
                    val photoUrl = uri.toString()
                    mFirebaseHelper.updateUserPhoto(photoUrl){
                                mUser = mUser.copy(photo = photoUrl)
                                profile_image.loadUserPhoto(mUser.photo)
                    }
                }
            }
        }
    }

    private fun updateProfile(){
        // get user from inputs
        mPendingUser = readInputs()
        //validate
        val error = validate(mPendingUser)
        if(error==null){
            //save mPendingUser
            if(mPendingUser.email == mUser.email){
                //update mPendingUser
                updateUser(mPendingUser)
            }else{
                // re-enter password
                PasswordDialog().show(supportFragmentManager,"password_dialog")
            }
        }else{
            showToast(error)
        }
    }

    private fun readInputs(): User{
        return User(
            name = name_input.text.toString(),
            username = username_input.text.toString(),
            email = email_input.text.toString(),
            website = web_input.text!!.toStringOrNull(),
            bio = bio_input.text!!.toStringOrNull(),
            phone = number_input.text.toStringOrNull(),
            gender = gender_input.text.toStringOrNull()
        )
    }
    override fun onPasswordConfirm(password: String) {
        if(password.isNotEmpty()) {
            // re-auth
            val credential = EmailAuthProvider.getCredential(mUser.email, password)
            mFirebaseHelper.reauthenticate(credential){
                // update email in auth
                mFirebaseHelper.updateEmail(mPendingUser.email){
                    // update user
                    updateUser(mPendingUser)
                }
            }
        }else{
            showToast("You should enter wrong password")
        }
    }

   private fun updateUser(user: User){
       val updateMap = mutableMapOf<String, Any?>()

       if(user.bio != mUser.bio) updateMap["bio"] = user.bio
       if(user.email != mUser.email) updateMap["email"] = user.email
       if(user.gender != mUser.gender) updateMap["gender"] = user.gender
       if(user.name != mUser.name) updateMap["name"] = user.name
       if(user.phone != mUser.phone) updateMap["phone"] = user.phone
       if(user.username != mUser.username) updateMap["username"] = user.username
       if(user.website != mUser.website) updateMap["website"] = user.website
       showToast("Profile saved")
       finish()

       mFirebaseHelper.updateUser(updateMap){
               showToast("Profile saved")
               finish()
       }
   }

    private fun validate(user: User): String?  = when {
            user.name.isEmpty() -> "Please enter your namae"
            user.username.isEmpty() -> "Please enter your username"
            user.email.isEmpty() -> "Please enter your email"
            else -> null
    }
}