package com.wenull.meetcrypt_students.ui.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.wenull.meetcrypt_students.R
import com.wenull.meetcrypt_students.data.auth.PhoneAuthViewModel
import com.wenull.meetcrypt_students.data.auth.PhoneAuthViewModelFactory
import com.wenull.meetcrypt_students.data.firebase.FirebaseSource

class PhoneAuthActivity : AppCompatActivity(){



    private lateinit var viewModel: PhoneAuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_auth)
        val firebaseAuthSource =
            FirebaseSource()
        val sharedPreferences = this.getSharedPreferences("com.example.meetcryptforstudents", Context.MODE_PRIVATE)
        val factory = PhoneAuthViewModelFactory(
            firebaseAuthSource,
            sharedPreferences
        )
        viewModel = ViewModelProvider(this,factory).get(PhoneAuthViewModel::class.java)
        viewModel.message.observe(this, Observer {
            it.getContentIfNotHandled().let {
                if (it != null)
                    Toast.makeText(this, it , Toast.LENGTH_SHORT).show()

            }


        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        viewModel.phoneNumber.value=""
        viewModel.otp.value=""
    }
}