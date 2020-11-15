package com.wenull.meetcrypt_students.data.auth

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wenull.meetcrypt_students.data.auth.PhoneAuthViewModel
import com.wenull.meetcrypt_students.data.firebase.FirebaseSource
import java.lang.IllegalArgumentException

class PhoneAuthViewModelFactory(private val firebaseSource: FirebaseSource,
                                private val sharedPreferences: SharedPreferences): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PhoneAuthViewModel::class.java)){
            return PhoneAuthViewModel(
                firebaseSource,
                sharedPreferences
            ) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}