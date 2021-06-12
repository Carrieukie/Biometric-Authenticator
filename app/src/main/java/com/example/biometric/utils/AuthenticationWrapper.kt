package com.example.biometric.utils

import androidx.appcompat.app.AppCompatActivity
import com.example.biometric.biometrics.BiometricAuthenticator
import com.example.biometric.biometrics.BiometricCallback
import com.example.biometric.lockscreen.LockScreenAuthenticator
import com.example.biometric.lockscreen.LockScreenCallback

class AuthenticationWrapper(activity: AppCompatActivity, biometricCallback: BiometricCallback, lockScreenCallback: LockScreenCallback) {

    val lockScreenAuthenticator by lazy {
        LockScreenAuthenticator(activity, lockScreenCallback).apply {
            createKey()
        }

    }
    val biometricAuthenticator by lazy {
        BiometricAuthenticator(activity , biometricCallback, lockScreenAuthenticator)
    }


}