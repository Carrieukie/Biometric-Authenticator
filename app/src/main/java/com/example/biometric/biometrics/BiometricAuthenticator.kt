
package com.example.biometric.biometrics

import android.content.Intent
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.biometric.MainActivity
import com.example.biometric.lockscreen.LockScreenAuthenticator
import com.example.biometric.toast
import java.util.concurrent.Executor

/**
 * This is the code of our biometric 'wrapper' class
 * @param activity is the activity initializing this class
 * @param biometricCallback is still the activity initializing this class but should implement the BiometricCallback interface
 */
class BiometricAuthenticator(
    private val activity: AppCompatActivity,
    private val biometricCallback: BiometricCallback,
    private val lockScreenAuthenticator: LockScreenAuthenticator) {

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
        checkBiometricsSupport()
    }


    // Checks the state of the biometrics using biometric manager
    private fun checkBiometricsSupport(){

        val biometricManager = BiometricManager.from(activity)


        when (biometricManager.canAuthenticate()) {

            BiometricManager.BIOMETRIC_SUCCESS -> {

                //Just use biometrics
                biometricPrompt.authenticate(promptInfo)

                activity.toast("App can authenticate using biometrics.")
            }

            //we can use lockscreen authenticator for the following usescases

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {

                lockScreenAuthenticator.showAuthenticationScreen()

                activity.toast("MY_APP_TAG No biometric features available on this device.")
            }

            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {

                lockScreenAuthenticator.showAuthenticationScreen()
                activity.toast("Biometric error, security Update required")
            }

            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {

                lockScreenAuthenticator.showAuthenticationScreen()

                activity.toast("Biometric status unknown")
            }

            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {

                lockScreenAuthenticator.showAuthenticationScreen()
                activity.toast("Biometric Not supported on this device")
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {

                lockScreenAuthenticator.showAuthenticationScreen()
                activity.toast("Biometric features are currently unavailable.")
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
                lockScreenAuthenticator.showAuthenticationScreen()
            }

        }
    }


    //Opens settings activity for the user to add biometrics android 11 and above
    private fun startFingerprintEnrollment() {
        val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
            putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
        }
        activity.startActivityForResult(enrollIntent, 123)
    }

    //Opens settings activity for the user to add biometrics below android 11
    private fun gotoSecuritySettings() {

        val intent = Intent(Settings.ACTION_SECURITY_SETTINGS)
        activity.startActivityForResult(intent, REQUESTCODE_SECURITY_SETTINGS)

    }

    //companion object
    companion object {

        const val REQUESTCODE_SECURITY_SETTINGS = 123
    }

}