package com.ryspay.nurda.screens

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CommonViewModel: ViewModel() {
    private val _eroorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _eroorMessage
}