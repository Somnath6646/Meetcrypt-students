package com.wenull.meetcrypt_students.data.main

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wenull.meetcrypt_students.data.firebase.FirebaseSource
import java.lang.IllegalArgumentException

class MainViewModelFactory(private val firebaseSource: FirebaseSource,
                           private val sharedPreferences: SharedPreferences
): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)){
            return MainViewModel(
                firebaseSource,
                sharedPreferences
            ) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}