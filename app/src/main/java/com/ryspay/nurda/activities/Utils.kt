package com.ryspay.nurda.activities

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.ryspay.nurda.R
import kotlinx.android.synthetic.main.fragment_register_email.view.*
import org.w3c.dom.Text

class ValueEventListenerAdapter(val handler: (DataSnapshot) -> Unit): ValueEventListener {
    private val TAG = "ValueEventListenerAdapt"

    override fun onDataChange(data: DataSnapshot) {
        handler(data)
    }

    override fun onCancelled(error: DatabaseError) {
        Log.e(TAG, "onDataChange: ",error.toException())
    }
}

fun Context.showToast(text: String, duration: Int = Toast.LENGTH_SHORT){
    Toast.makeText(this, text, duration).show()
}
@GlideModule
class CustomGlideModule: AppGlideModule()

fun coordinateBtnAndInputs(btn: Button, vararg inputs: EditText){
    btn.isEnabled = inputs.all { it.text.isNotEmpty() }
    val watcher = object: TextWatcher{
        override fun afterTextChanged(p0: Editable?) {
            btn.isEnabled = inputs.all { it.text.isNotEmpty() }
        }
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    }
    inputs.forEach { it.addTextChangedListener(watcher) }
}

fun ImageView.loadUserPhoto(photoUrl: String?){
    if(!(context as Activity).isDestroyed) {
        GlideApp.with(this).load(photoUrl).fallback(R.drawable.person).into(this)
    }
}

fun Editable.toStringOrNull(): String?{
    val str = toString()
    return if(str.isEmpty())  null else str
}