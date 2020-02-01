package com.kasta.nangkring

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit
import kotlinx.android.synthetic.main.activity_main.phone_number_text
import kotlinx.android.synthetic.main.activity_main.country_code_text

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var verificationInProgress = false
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:$credential")
                verificationInProgress = false
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w(TAG, "onVerificationFailed", e)
                verificationInProgress = false
                if (e is FirebaseAuthInvalidCredentialsException) {
                    phone_number_text.error = "Invalid phone number."
                } else if (e is FirebaseTooManyRequestsException) {
                    println("Quota exceeded.")
                }
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                storedVerificationId = verificationId
                val intent = Intent(this@MainActivity, OtpActivity::class.java)
                intent.putExtra("AuthCredentials", storedVerificationId)
                startActivity(intent)
            }

        }

        val btnGenerate: Button = findViewById(R.id.generate_btn)

        btnGenerate.setOnClickListener{
            val countryCode: EditText = findViewById(R.id.country_code_text)
            val fieldPhoneNumber: EditText = findViewById(R.id.phone_number_text)

            val phoneNumber = "+" + countryCode.text.toString() + fieldPhoneNumber.text.toString()

            startPhoneNumberVerification(phoneNumber)
        }
    }

    override fun onStart() {
        super.onStart()

        if(verificationInProgress && validatePhoneNumber()) {
            val phoneNumber = "+" + country_code_text.text.toString() + phone_number_text.text.toString()
            startPhoneNumberVerification(phoneNumber)
        }
    }

    private fun validatePhoneNumber(): Boolean {
        val phoneNumber = phone_number_text.text.toString()
        if (TextUtils.isEmpty(phoneNumber)) {
            phone_number_text.error = "Invalid phone number."
            return false
        }

        return true
    }

    private fun startPhoneNumberVerification(phoneNumber: String){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber, 60, TimeUnit.SECONDS, this, callbacks
        )

        verificationInProgress = true
    }

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when(v.id) {
                R.id.generate_btn -> {
                    if (!validatePhoneNumber()) {
                        return
                    }

                    val countryCode: EditText = findViewById(R.id.country_code_text)
                    val fieldPhoneNumber: EditText = findViewById(R.id.phone_number_text)

                    val phoneNumber = "+" + countryCode.text.toString() + fieldPhoneNumber.text.toString()

                    startPhoneNumberVerification(phoneNumber)
                }
            }
        }
    }
}
