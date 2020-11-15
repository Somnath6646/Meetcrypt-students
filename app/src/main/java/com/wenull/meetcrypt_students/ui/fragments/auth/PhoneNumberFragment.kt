package com.wenull.meetcrypt_students.ui.fragments.auth

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.wenull.meetcrypt_students.R
import com.wenull.meetcrypt_students.data.auth.NavigationListener
import com.wenull.meetcrypt_students.data.auth.PhoneAuthViewModel
import com.wenull.meetcrypt_students.databinding.FragmentPhoneNumberBinding
import com.wenull.meetcrypt_students.ui.base.BaseFragment


class PhoneNumberFragment : BaseFragment<FragmentPhoneNumberBinding, PhoneAuthViewModel>(),
    NavigationListener {

    private lateinit var avdProgress: AnimatedVectorDrawableCompat

    override fun getFragmentView(): Int = R.layout.fragment_phone_number

    override fun getViewModel(): Class<PhoneAuthViewModel> = PhoneAuthViewModel::class.java

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        initializeAvd()


        viewModel.setNavigationListener(this)


        viewModel.progressBarVisible_PHONE.observe(viewLifecycleOwner, Observer {
            binding.image.visibility = it

            binding.verify.isEnabled = it != View.VISIBLE

        })

        binding.verify.setOnClickListener(View.OnClickListener {
            viewModel.verify()
        })
    }



    override fun navigateToOTPFragment() {
        val action =
            PhoneNumberFragmentDirections.actionPhoneNumberFragmentToOTPFragment()
        findNavController().navigate(action)
    }

    override fun navigateToCredentialsFragment() {

    }

    override fun navigateToMainActivity() {

    }

    var action = Runnable { repeatAnimation() }

    private fun initializeAvd() {
        avdProgress = AnimatedVectorDrawableCompat.create(requireContext(), R.drawable.avd)!!
        binding.image.setBackground(avdProgress)
        repeatAnimation()
    }

    private fun repeatAnimation() {
        avdProgress.start()
        binding.image.postDelayed(action, 1000) // Will repeat animation in every 1 second
    }
}