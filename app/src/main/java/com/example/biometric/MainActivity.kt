package com.example.biometric

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager.Authenticators.*
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.AUTHENTICATION_RESULT_TYPE_BIOMETRIC
import androidx.biometric.BiometricPrompt.AUTHENTICATION_RESULT_TYPE_DEVICE_CREDENTIAL
import com.example.biometric.biometrics.BiometricAuthenticator
import com.example.biometric.biometrics.BiometricCallback
import com.example.biometric.lockscreen.LockScreenAuthenticator
import com.example.biometric.lockscreen.LockScreenCallback
import com.example.biometric.utils.AuthenticationWrapper


class MainActivity : AppCompatActivity(), BiometricCallback, LockScreenCallback {

    private val authenticationWrapper by lazy {
        AuthenticationWrapper(this, this, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Get view by id
        val biometricLoginButton = findViewById<Button>(R.id.biometric_login)

        //set OnclickListener
        biometricLoginButton.setOnClickListener {

            authenticationWrapper.biometricAuthenticator.authenticate()

        }
    }

    /**
     * This three callbacks are for biometric authentication
     */
    //Error occured during authentication
    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {

        /**
         * This when expression handles all the error codes if an authentication error occurs
         * @see {@https://developer.android.com/reference/android/hardware/biometrics/BiometricPrompt}
         */

        when (errorCode) {

            AUTHENTICATION_RESULT_TYPE_BIOMETRIC -> { }

            AUTHENTICATION_RESULT_TYPE_DEVICE_CREDENTIAL -> { }

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

    /**
     * This callback methods is invoked  when login screen with PIN is needed
     */

    override fun showAuthenticationScreen() {

        // Create the Confirm Credentials screen. You can customize the title and description. Or
        // we will provide a generic one for you if you leave it null

        val mKeyguardManager: KeyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        val intent: Intent = mKeyguardManager.createConfirmDeviceCredentialIntent(null, null)

        startActivityForResult(intent, LockScreenAuthenticator.REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LockScreenAuthenticator.REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS) {

            // Challenge completed, proceed with using cipher
            if (resultCode == RESULT_OK) {
                if (authenticationWrapper.lockScreenAuthenticator.tryEncrypt()) {
                    toast("Lock screen authentication succeed")
                }
            } else {
                // The user canceled or didn’t complete the lock screen
                // operation. Go to error/cancellation flow.
                toast("Authentication failed")
            }

        }
    }


}