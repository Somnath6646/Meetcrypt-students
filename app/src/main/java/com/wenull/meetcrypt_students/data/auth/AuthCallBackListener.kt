package com.wenull.meetcrypt_students.data.auth

import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.wenull.meetcrypt_students.utils.models.StudentCredentials
import com.wenull.meetcrypt_students.utils.models.TeacherCredentials
import java.lang.Exception

interface AuthCallBackListener {

    fun onVerificationCompleted(credential: PhoneAuthCredential)
    fun onVerificationFailed(e: FirebaseException)
    fun onCodeSent(verificationId: String, resendingToken: PhoneAuthProvider.ForceResendingToken)
    fun signInSucess()
    fun signInFailed(e: Exception)

    fun storeCredentials(teacherCredentials: StudentCredentials)
    fun redirectToCredentialsFrag()
    fun redirectToMainActivity()
    fun makeProgressBarInVisible()
}