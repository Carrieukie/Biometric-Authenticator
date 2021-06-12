
package com.example.biometric

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.*
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.AUTHENTICATION_RESULT_TYPE_BIOMETRIC
import androidx.biometric.BiometricPrompt.AUTHENTICATION_RESULT_TYPE_DEVICE_CREDENTIAL
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

        //Check if the system has BiometricSupport
        checkBiometricsSupport()

        //create an instance of our Biometric authenticator
        /**
         * @param this is an activity,,,,could be also a fragment
         * @param this is an implementation of Biometriccallback
         */
        val biometricAuthenticator = BiometricAuthenticator(this, this)

        //set OnclickListener
        biometricLoginButton.setOnClickListener {
            biometricAuthenticator.authenticate()
        }
    }

    //Error occured during authentication
    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {

        /**
         * This when expression handles all the error codes if authentication error occurs
         * @see https://developer.android.com/reference/android/hardware/biometrics/BiometricPrompt
         */
        when(errorCode){

            AUTHENTICATION_RESULT_TYPE_BIOMETRIC -> {

            }

            AUTHENTICATION_RESULT_TYPE_DEVICE_CREDENTIAL -> {

            }

            //..... etc Customize this based on your use cases

        }
    }

    //Authentication was successful
    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
        toast("Success")
    }

    //Authentication failed
    override fun onAuthenticationFailed() {
        toast("Authentication Failed")
    }

    //toast a message
    private fun toast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    // Checks the state of the biometrics using biometric manager
    private fun checkBiometricsSupport(){
        val biometricManager = BiometricManager.from(this)


        when (biometricManager.canAuthenticate()) {

            BiometricManager.BIOMETRIC_SUCCESS -> {
                toast("App can authenticate using biometrics.")
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                toast("MY_APP_TAG No biometric features available on this device.")
            }

            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                toast("Biometric error, security Update required")
            }

            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                toast("Biometric status unknown")
            }

            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                toast("Biometric Not supported on this device")
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                toast("Biometric features are currently unavailable.")
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // Prompts the user to create credentials that your app accepts.
                try {
                    startFingerprintEnrollment()
                }catch (e : Exception){
                    gotoSecuritySettings()
                }

            }

            else ->{
                toast("God help me!!")
            }

        }
    }


    //Opens settings activity for the user to add biometrics android 11 and above
    private fun startFingerprintEnrollment() {
        val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
            putExtra(
                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                BIOMETRIC_STRONG or DEVICE_CREDENTIAL
            )
        }
        startActivityForResult(enrollIntent, 123)
    }

    //Opens settings activity for the user to add biometrics below android 11
    private fun gotoSecuritySettings() {
        val intent = Intent(Settings.ACTION_SECURITY_SETTINGS)
        startActivityForResult(intent, REQUESTCODE_SECURITY_SETTINGS)
    }

    //companion object
    companion object {

        const val REQUESTCODE_SECURITY_SETTINGS = 123
    }

}