package com.ryspay.nurda.activities

import PasswordDialog
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.FileProvider
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ryspay.nurda.R
import com.ryspay.nurda.models.User
import kotlinx.android.synthetic.main.activity_edit_profile.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class EditProfileActivity : AppCompatActivity(), View.OnClickListener, PasswordDialog.Listener {
    private lateinit var mImageUri: Uri

    private val TAKE_PICTURE_REQUEST_CODE = 1
    private val TAG = "EditProfileActivity"

    private lateinit var mUser: User
    private lateinit var mPendingUser: User

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private lateinit var mStorage: StorageReference

    val simpleDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        Log.d(TAG, "onCreate: ");

        close_image.setOnClickListener (this)
        save_image.setOnClickListener(this)
        chnage_photo_lable.setOnClickListener(this)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference
        mStorage = FirebaseStorage.getInstance().reference

        mDatabase.child("users").child(mAuth.currentUser!!.uid)
            .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                mUser = it.getValue(User::class.java)!!
                name_input.setText(mUser.name, TextView.BufferType.EDITABLE)
                username_input.setText(mUser.username, TextView.BufferType.EDITABLE)
                web_input.setText(mUser.website, TextView.BufferType.EDITABLE)
                bio_input.setText(mUser.bio, TextView.BufferType.EDITABLE)
                email_input.setText(mUser.email, TextView.BufferType.EDITABLE)
                number_input.setText(mUser.phone, TextView.BufferType.EDITABLE)
                gender_input.setText(mUser.gender, TextView.BufferType.EDITABLE)
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
                takeCameraPicture()
            }
        }
    }

    private fun takeCameraPicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(intent.resolveActivity(packageManager) != null){
            val imageFile = createImageFile()
            mImageUri = FileProvider.getUriForFile(this, "com.ryspay.nurda.fileprovider", imageFile)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri)
            startActivityForResult(intent, TAKE_PICTURE_REQUEST_CODE)
        }
    }

    private fun createImageFile(): File {
        // Create an image file name
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${simpleDateFormat.format(Date())}_",
            ".jpg",
            storageDir
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == TAKE_PICTURE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            // upload image to FirebaseStorage
            val uid = mAuth.currentUser!!.uid
            mStorage.child("users/$uid/photo").putFile(mImageUri).addOnCompleteListener{
                if(it.isSuccessful){
                    val downloadTask = it.result!!.metadata!!.reference!!.downloadUrl
                    downloadTask.addOnSuccessListener {uri ->
                        val photoUrl = uri.toString()
                        mDatabase.child("users/$uid/photo").setValue(photoUrl)
                            .addOnCompleteListener {task ->
                                if(task.isSuccessful){
                                    mUser = mUser.copy(photo = photoUrl)
                                    profile_image.loadUserPhoto(mUser.photo)
                                }else{
                                    showToast(task.exception!!.message!!)
                                }
                            }
                    }
                }else{
                    showToast(it.exception!!.message!!)
                }
            }
            // save image to database user.photo
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
            mAuth.currentUser!!.reauthenticate(credential){
                // update email in auth
                mAuth.currentUser!!.updateEmail(mPendingUser.email){
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

       mDatabase.updateUser(mAuth.currentUser!!.uid,updateMap){
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

    private fun FirebaseUser.updateEmail(email: String, onSuccessListener: () ->  Unit){
        updateEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccessListener()
            } else {
                //showToast(it.exception!!.message!!)
                showToast("You enter wrong password")
                Log.d(TAG, "onPasswordConfirm: failure faze 2")
            }
        }
    }

    private fun DatabaseReference.updateUser(uid:String, updatesMap: Map<String,Any?>, onSuccessListener: () -> Unit){
        child("users").child(uid).updateChildren(updatesMap).addOnCompleteListener {
            if(it.isSuccessful){
                onSuccessListener()
            }else{
                showToast(it.exception!!.message!!)
            }
        }
    }

    private fun FirebaseUser.reauthenticate(credential: AuthCredential, onSuccessListener: () ->  Unit) {
        reauthenticate(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccessListener()
            } else {
                //showToast(it.exception!!.message!!)
                showToast("You entered wrong password")
                Log.d(TAG, "onPasswordConfirm: failure faze 2")
            }
        }
    }

}



