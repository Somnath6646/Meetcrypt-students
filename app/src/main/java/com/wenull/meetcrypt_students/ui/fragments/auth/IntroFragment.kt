package com.wenull.meetcrypt_students.ui.fragments.auth

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.wenull.meetcrypt_students.R
import com.wenull.meetcrypt_students.data.auth.PhoneAuthViewModel
import com.wenull.meetcrypt_students.databinding.FragmentIntroBinding
import com.wenull.meetcrypt_students.ui.base.BaseFragment

class IntroFragment : BaseFragment<FragmentIntroBinding, PhoneAuthViewModel>() {

    override fun getFragmentView(): Int = R.layout.fragment_intro

    override fun getViewModel(): Class<PhoneAuthViewModel> = PhoneAuthViewModel::class.java

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        binding.proceed.setOnClickListener {
            val action =
                IntroFragmentDirections.actionIntroFragmentToPhoneNumberFragment()
            findNavController().navigate(action)
        }
    }
}