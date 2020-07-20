package com.ryspay.nurda.screens.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnFailureListener
import java.lang.Exception

class CommonViewModel: ViewModel(), OnFailureListener {
    private val _eroorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _eroorMessage

    override fun onFailure(e: Exception) {
        _eroorMessage.value = e.message
    }
}