package com.wenull.meetcrypt_students.data.auth

import android.content.SharedPreferences
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.wenull.meetcrypt_students.data.auth.AuthCallBackListener
import com.wenull.meetcrypt_students.data.firebase.FirebaseSource
import com.wenull.meetcrypt_students.utils.helpers.Event
import com.wenull.meetcrypt_students.utils.helpers.ObjectSerializer
import com.wenull.meetcrypt_students.utils.models.StudentCredentials

class PhoneAuthViewModel(private val firebaseSource: FirebaseSource,
                         private val sharedPreferences: SharedPreferences) : ViewModel(), Observable,
    AuthCallBackListener {

    private lateinit var navigateListener: NavigationListener

    fun setNavigationListener(navlistener: NavigationListener) {
        this.navigateListener = navlistener
    }

    var progressBarVisible_OTP: MutableLiveData<Int> = MutableLiveData<Int>(View.GONE)
    var progressBarVisible_PHONE: MutableLiveData<Int> = MutableLiveData<Int>(View.GONE)
    @Bindable
    val phoneNumber = MutableLiveData<String>()

    @Bindable
    val otp = MutableLiveData<String>()

    @Bindable
    val studentName = MutableLiveData<String>()

    @Bindable
    val isClass9checked = MutableLiveData<Boolean>(false)
    @Bindable
    val isClass10checked = MutableLiveData<Boolean>(false)
    @Bindable
    val isClass11checked = MutableLiveData<Boolean>(false)
    @Bindable
    val isClass12checked = MutableLiveData<Boolean>(false)

    private var classStudying = 0




    private val fireBaseAuth by lazy{
        FirebaseAuth.getInstance()
    }

    init {
        firebaseSource.setAuthCallBackListener(this)
    }


    private val validationMessage = MutableLiveData<Event<String>>()

    val message: LiveData<Event<String>>
    get() = validationMessage



    private fun resendVerificationCode(
        phoneNumber: String
    ) {

    }




    fun verify(){


       if(phoneNumber.value == null){
           validationMessage.value=
               Event("Please enter a phone number")

       }else if(!Patterns.PHONE.matcher(phoneNumber.value!!).matches()){
           Event("Please enter a proper phone number")
       }
       else {
           progressBarVisible_PHONE.value = View.VISIBLE
           firebaseSource.sendVerificationCode(phoneNumber.value!!)
       }


   }


    fun verifyOTP(){
        progressBarVisible_OTP.value = View.VISIBLE
        firebaseSource.verifyVerificationCode(otp.value!!)
    }

    fun continueWIthCredentials(){
        if(isClass9checked.value!!) classStudying = 9
        if(isClass10checked.value!!) classStudying = 10
        if(isClass11checked.value!!) classStudying = 11
        if(isClass12checked.value!!) classStudying = 12

        if(studentName.value != null && classStudying != 0) {
            val studentCredentials = StudentCredentials(fireBaseAuth.currentUser?.uid.toString(), fireBaseAuth.currentUser?.phoneNumber.toString(), studentName.value!!, classStudying)
            firebaseSource.setValueToCredentials(studentCredentials)
            navigateListener.navigateToMainActivity()
        }else{
            validationMessage.value =
                Event("Enter all details")
        }


    }



    override fun onVerificationCompleted(credential: PhoneAuthCredential) {

        validationMessage.value =
            Event("OTP Verification done automatically")

        progressBarVisible_OTP .value = View.VISIBLE
        progressBarVisible_PHONE.value = View.GONE
    }

    override fun onVerificationFailed(e: FirebaseException) {

        if (e is FirebaseAuthInvalidCredentialsException) {
            validationMessage.value =
                Event("Invalid request")
        } else if (e is FirebaseTooManyRequestsException) {
            validationMessage.value =
                Event("SMS quota exceeded")
        } else{
            validationMessage.value =
                Event("Phone number verification failed")
        }
        Log.i("error",e.message.toString())
        progressBarVisible_PHONE.value = View.GONE
    }

    override fun onCodeSent(
        verificationId: String,
        resendingToken: PhoneAuthProvider.ForceResendingToken
    ) {
        progressBarVisible_PHONE.value = View.GONE
        validationMessage.value =
            Event("Code Sent")
        navigateListener.navigateToOTPFragment()

    }

    override fun signInSucess() {

        firebaseSource.checkIfUserExists(fireBaseAuth.currentUser?.uid.toString())

        validationMessage.value =
            Event("Sign In sucessfull")


    }

    override fun signInFailed(e: Exception) {

        validationMessage.value =
            Event("Sign In Failed")

    }

    override fun makeProgressBarInVisible() {
        progressBarVisible_OTP.value = View.GONE
    }

    override fun storeCredentials(studentCredentials: StudentCredentials) {
        Log.i("came in ", "storeCredentials")

        sharedPreferences.edit().putString("Student'sCredential", ObjectSerializer.serialize(studentCredentials)).apply()
    }

    override fun redirectToCredentialsFrag() {
        navigateListener.navigateToCredentialsFragment()
    }

    override fun redirectToMainActivity() {
        navigateListener.navigateToMainActivity()
    }


    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }






}

interface NavigationListener{
    fun navigateToOTPFragment()
    fun navigateToCredentialsFragment()
    fun navigateToMainActivity()
}