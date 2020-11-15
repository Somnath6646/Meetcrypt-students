package com.wenull.meetcrypt_students.ui.fragments.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.wenull.meetcrypt_students.ui.activities.MainActivity
import com.wenull.meetcrypt_students.R
import com.wenull.meetcrypt_students.data.auth.NavigationListener
import com.wenull.meetcrypt_students.data.auth.PhoneAuthViewModel
import com.wenull.meetcrypt_students.databinding.FragmentOTPBinding
import com.wenull.meetcrypt_students.ui.base.BaseFragment

class OTPFragment: BaseFragment<FragmentOTPBinding, PhoneAuthViewModel>(), NavigationListener {

    private lateinit var avdProgress: AnimatedVectorDrawableCompat

    override fun getFragmentView(): Int = R.layout.fragment_o_t_p

    override fun getViewModel(): Class<PhoneAuthViewModel> = PhoneAuthViewModel::class.java

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.setNavigationListener(this)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        initializeAvd()

        viewModel.progressBarVisible_OTP.observe(viewLifecycleOwner, Observer {
            binding.image.visibility = it
            binding.proceedOtp.isEnabled = it != View.VISIBLE
        })


        binding.proceedOtp.setOnClickListener {
            viewModel.verifyOTP()
        }
    }

    override fun navigateToOTPFragment() {

    }

    override fun navigateToCredentialsFragment() {
        val action =
            OTPFragmentDirections.actionOTPFragmentToCredentialsFragment()
        findNavController().navigate(action)
    }

    override fun navigateToMainActivity() {
        val intent = Intent(context, MainActivity:: class.java)
        startActivity(intent)
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