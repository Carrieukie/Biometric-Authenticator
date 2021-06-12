
package com.example.biometric

import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor

/**
 * This is the code of our biometric 'wrapper' class
 * @param activity is the activity initializing this class
 * @param biometricCallback is still the activity initializing this class but should implement the BiometricCallback interface
 */
class BiometricAuthenticator(activity: MainActivity, biometricCallback: BiometricCallback) {

    //Initialize a class that manages a system-provided biometric prompt.
    private val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Biometric login for my app")
        .setSubtitle("Log in using your biometric credential")
        .setNegativeButtonText("Use account password")
        .build()

    //Returns an Executor that will run enqueued tasks on the main thread associated with this context
    private var executor: Executor = ContextCompat.getMainExecutor(activity)

    //Implement AuthenticationCallback
    private val biometricPrompt =
        BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)

                //notify the implementation passed in that a fail occurred
                biometricCallback.onAuthenticationError(errorCode, errString)
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)

                //notify the implementation passed on that a fail occurred
                biometricCallback.onAuthenticationSucceeded(result)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()

                //notify the implementation passed in that a fail occurred
                biometricCallback.onAuthenticationFailed()
            }
        })


    //Call this to authenticate
    fun authenticate() {
        biometricPrompt.authenticate(promptInfo)
    }
}