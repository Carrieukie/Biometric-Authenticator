package com.example.biometric

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity(), BiometricCallback {
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Get view by id
        val biometricLoginButton = findViewById<Button>(R.id.biometric_login)

        //create an instance of our Biometric authenticator
        /**
         * @param this is an activity,,,,could be also a fragment
         * @param this is an implementation of Biometriccallback
         */
        val biometricAuthenticator = BiometricAuthenticator(this,this)

        //set OnclickListener
        biometricLoginButton.setOnClickListener {
            biometricAuthenticator.authenticate()
        }
    }

    //Error occured during authentication
    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
        toast(errString.toString())
    }

    //Authentication was successful
    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
        toast("Success")
    }

    //Authentication failedgit
    override fun onAuthenticationFailed() {
        toast("Authentication Failed")
    }

    private fun toast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

//    fun checkBiometricsSupport(){
//        val biometricManager = BiometricManager.from(this)
//
//        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
//            BiometricManager.BIOMETRIC_SUCCESS ->{
//                Log.d("MY_APP_TAG", "App can authenticate using biometrics.")
//                biometricsOne()
//            }
//
//            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
//                Log.e("MY_APP_TAG", "No biometric features available on this device.")
//            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
//                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.")
//            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
//                // Prompts the user to create credentials that your app accepts.
//                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
//                    putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
//                            BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
//                }
//                startActivityForResult(enrollIntent, 123)
//            }
//
//        }
//    }
}