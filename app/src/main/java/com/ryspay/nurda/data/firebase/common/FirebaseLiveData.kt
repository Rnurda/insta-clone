package com.ryspay.nurda.data.firebase.common

import androidx.lifecycle.LiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.ryspay.nurda.common.ValueEventListenerAdapter

class FirebaseLiveData(private val reference : DatabaseReference): LiveData<DataSnapshot>(){
    private val listener = ValueEventListenerAdapter {
        value = it
    }

    override fun onActive() {
        super.onActive()
        reference.addValueEventListener(listener)
    }

    override fun onInactive() {
        super.onInactive()
        reference.removeEventListener(listener)
    }
}