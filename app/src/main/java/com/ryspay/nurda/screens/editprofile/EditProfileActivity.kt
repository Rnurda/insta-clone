package com.ryspay.nurda.screens.editprofile

import PasswordDialog
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import com.google.firebase.auth.EmailAuthProvider
import com.ryspay.nurda.R
import com.ryspay.nurda.models.User
import com.ryspay.nurda.screens.common.BaseActivity
import com.ryspay.nurda.screens.common.loadUserPhoto
import com.ryspay.nurda.screens.common.showToast
import com.ryspay.nurda.screens.common.toStringOrNull
import com.ryspay.nurda.screens.common.CameraHelper
import kotlinx.android.synthetic.main.activity_edit_profile.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class EditProfileActivity : BaseActivity(), View.OnClickListener, PasswordDialog.Listener {
    private lateinit var mViewModel: EditProfielViewModel
    private lateinit var mUser: User
    private lateinit var mPendingUser: User
    private lateinit var cameraPictureTaker: CameraHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        Log.d(TAG, "onCreate: ");

        cameraPictureTaker = CameraHelper(this)

        mViewModel = initViewModel()
        mViewModel.user.observe(this, Observer{
            it.let{
                mUser = it
                name_input.setText(mUser.name)
                username_input.setText(mUser.username)
                web_input.setText(mUser.website)
                bio_input.setText(mUser.bio)
                email_input.setText(mUser.email)
                number_input.setText(mUser.phone)
                gender_input.setText(mUser.gender)
                profile_image.loadUserPhoto(mUser.photo)
            }
        })

        close_image.setOnClickListener (this)
        save_image.setOnClickListener(this)
        chnage_photo_lable.setOnClickListener(this)

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
            mViewModel.uploadAndSetUserPhoto(cameraPictureTaker.imageUri!!)
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
            mViewModel.updateEmail(currentEmail = mUser.email, newEmail = mPendingUser.email,password = password)
                .addOnSuccessListener { updateUser(mPendingUser) }
        }else{
            showToast(getString(R.string.you_enter_wrong_password))
        }
    }

   private fun updateUser(user: User){

       mViewModel.updateUserProfile(currentUser = mUser, newUser = user)
           .addOnSuccessListener {
               showToast(getString(R.string.profile_saved))
               finish()
           }
   }

    private fun validate(user: User): String?  = when {
            user.name.isEmpty() -> getString(R.string.please_enter_your_name)
            user.username.isEmpty() -> getString(R.string.please_enter_your_username)
            user.email.isEmpty() -> getString(R.string.please_enter_your_email)
            else -> null
    }

    companion object{
        const val TAG = "EditProfileActivity"
    }

}