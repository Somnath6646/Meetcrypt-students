package com.wenull.meetcrypt_students.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.wenull.meetcrypt_students.R

class SplashActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        mAuth = FirebaseAuth.getInstance()
        /*val sharedPreferences = this.getSharedPreferences("com.example.meetcryptforstudents", Context.MODE_PRIVATE)

        sharedPreferences.edit().remove("Event_Cache")*/

        val goToPhoneAuthActivity = Intent(this, PhoneAuthActivity:: class.java)
        val goToMainActivity = Intent(this, MainActivity:: class.java)
        object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                if(mAuth.currentUser == null){
                    startActivity(goToPhoneAuthActivity)

                }else{

                    startActivity(goToMainActivity)
                }
                finish()
            }
        }.start()

    }
}