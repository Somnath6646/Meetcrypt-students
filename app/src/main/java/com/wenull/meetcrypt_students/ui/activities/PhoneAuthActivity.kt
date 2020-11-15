package com.wenull.meetcrypt_students.ui.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.wenull.meetcrypt_students.R
import com.wenull.meetcrypt_students.data.auth.PhoneAuthViewModel
import com.wenull.meetcrypt_students.data.auth.PhoneAuthViewModelFactory
import com.wenull.meetcrypt_students.data.firebase.FirebaseSource
import com.wenull.meetcrypt_students.databinding.ActivityPhoneAuthBinding
import com.wenull.meetcrypt_students.utils.helpers.NetworkObserver

class PhoneAuthActivity : AppCompatActivity() {


    private lateinit var viewModel: PhoneAuthViewModel
    private lateinit var binding: ActivityPhoneAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_phone_auth)

        val firebaseAuthSource =
            FirebaseSource()
        val sharedPreferences =
            this.getSharedPreferences("com.example.meetcryptforstudents", Context.MODE_PRIVATE)
        val factory = PhoneAuthViewModelFactory(
            firebaseAuthSource,
            sharedPreferences
        )
        viewModel = ViewModelProvider(this, factory).get(PhoneAuthViewModel::class.java)

        handleNetworkChanges()

        viewModel.message.observe(this, Observer {
            it.getContentIfNotHandled().let {
                if (it != null)
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()

            }


        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        viewModel.phoneNumber.value = ""
        viewModel.otp.value = ""
    }

    private fun handleNetworkChanges() {
        NetworkObserver.getNetworkLiveData(applicationContext)
            .observe(this, androidx.lifecycle.Observer { isConnected ->
                if (!isConnected) {
                    binding.textViewNetworkStatus.text = getString(R.string.text_no_connectivity)
                    binding.networkStatusLayout.apply {
                        binding.networkStatusLayout.visibility = View.VISIBLE
                        setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.colorStatusNotConnected
                            )
                        )
                    }
                } else {
                    binding.textViewNetworkStatus.text = getString(R.string.text_connectivity)
                    binding.networkStatusLayout.apply {
                        animate()
                            .alpha(1f)
                            .setStartDelay(1000)
                            .setDuration(1000)
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator?) {
                                    binding.networkStatusLayout.visibility = View.GONE
                                }
                            })
                        setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.colorStatusConnected
                            )
                        )
                    }
                }
            })
    }
}
