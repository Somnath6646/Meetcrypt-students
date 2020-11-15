package com.wenull.meetcrypt_students.ui.fragments.auth

import android.content.Intent
import android.os.Bundle
import com.wenull.meetcrypt_students.ui.activities.MainActivity
import com.wenull.meetcrypt_students.R
import com.wenull.meetcrypt_students.data.auth.NavigationListener
import com.wenull.meetcrypt_students.data.auth.PhoneAuthViewModel
import com.wenull.meetcrypt_students.databinding.FragmentCredentialsBinding
import com.wenull.meetcrypt_students.ui.base.BaseFragment

class CredentialsFragment: BaseFragment<FragmentCredentialsBinding, PhoneAuthViewModel>(),
    NavigationListener {
    override fun getFragmentView(): Int = R.layout.fragment_credentials

    override fun getViewModel(): Class<PhoneAuthViewModel> = PhoneAuthViewModel::class.java

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        viewModel.setNavigationListener(this)
    }

    override fun navigateToOTPFragment() {

    }

    override fun navigateToCredentialsFragment() {

    }

    override fun navigateToMainActivity() {
        val intent = Intent(context, MainActivity:: class.java)
        startActivity(intent)
        requireActivity().finish()
    }

}