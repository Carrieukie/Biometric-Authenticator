package com.example.biometric

import androidx.biometric.BiometricPrompt

interface BiometricCallback {

        fun onAuthenticationError(errorCode: Int, errString: CharSequence)

        fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult)

        fun onAuthenticationFailed()
}