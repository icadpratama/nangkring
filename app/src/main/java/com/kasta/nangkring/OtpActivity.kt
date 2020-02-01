package com.kasta.nangkring

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mCurrentUser: FirebaseUser

    private lateinit var mAuthCredentials: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        mAuth = FirebaseAuth.getInstance()

        mAuthCredentials = intent.getStringExtra("AuthCredentials")
        val otp: EditText = findViewById(R.id.otp_text_view)

        val mVerifyButton: Button = findViewById(R.id.verify_btn)

        mVerifyButton.setOnClickListener{
            val otp: String = otp.text.toString()
            val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(mAuthCredentials, otp)
            signInWithPhoneAuthCredential(credential)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    sendUserToHome()
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        println("Invalid code.")
                    }
                }
            }
    }

    private fun sendUserToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
    }

    companion object {
        private const val TAG = "OtpActivity"
    }
}
