package com.ryspay.nurda.activities

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.ryspay.nurda.R
import com.ryspay.nurda.utils.GlideApp

fun Context.showToast(text: String, duration: Int = Toast.LENGTH_SHORT){
    Toast.makeText(this, text, duration).show()
}

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

fun Editable.toStringOrNull(): String? {
    val str = toString()
    return if (str.isEmpty()) null else str
}
fun ImageView.loadImage(image: String?){
    GlideApp.with(this).load(image).centerCrop().into(this)
}