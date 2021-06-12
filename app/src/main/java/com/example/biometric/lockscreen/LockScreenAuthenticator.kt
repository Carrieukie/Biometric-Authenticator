package com.example.biometric.lockscreen

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.security.keystore.UserNotAuthenticatedException
import androidx.appcompat.app.AppCompatActivity
import com.example.biometric.toast
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.*


class LockScreenAuthenticator(private val activity: AppCompatActivity, private val lockScreenCallback: LockScreenCallback) {

     fun tryEncrypt(): Boolean {
        return try {
            val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            val secretKey: SecretKey = keyStore.getKey(KEY_NAME, null) as SecretKey
            val cipher: Cipher = Cipher.getInstance(
                KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/"
                        + KeyProperties.ENCRYPTION_PADDING_PKCS7
            )

            // Try encrypting something, it will only work if the user authenticated within
            // the last AUTHENTICATION_DURATION_SECONDS seconds.
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            cipher.doFinal(SECRET_BYTE_ARRAY)

            // If the user has recently authenticated, you will reach here.

            true
        } catch (e: UserNotAuthenticatedException) {
            // User is not authenticated, let's authenticate with device credentials.
            showAuthenticationScreen()
            false
        } catch (e: KeyPermanentlyInvalidatedException) {
            // This happens if the lock screen has been disabled or reset after the key was
            // generated after the key was generated.

            activity.toast(""" Keys are invalidated after created. Retry the purchase ${e.message}""".trimIndent())

            false
        } catch (e: BadPaddingException) {
            throw RuntimeException(e)
        } catch (e: IllegalBlockSizeException) {
            throw RuntimeException(e)
        } catch (e: KeyStoreException) {
            throw RuntimeException(e)
        } catch (e: CertificateException) {
            throw RuntimeException(e)
        } catch (e: UnrecoverableKeyException) {
            throw RuntimeException(e)
        } catch (e: IOException) {
            throw RuntimeException(e)
        } catch (e: NoSuchPaddingException) {
            throw RuntimeException(e)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        } catch (e: InvalidKeyException) {
            throw RuntimeException(e)
        }
    }

    /**
     * Creates a symmetric key in the Android Key Store which can only be used after the user has
     * authenticated with device credentials within the last X seconds.
     */
     fun createKey() {
        // Generate a key to decrypt payment credentials, tokens, etc.
        // This will most likely be a registration step for the user when they are setting up your app.
        try {
            val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            val keyGenerator: KeyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore"
            )

            // Set the alias of the entry in Android KeyStore where the key will appear
            // and the constrains (purposes) in the constructor of the Builder
            keyGenerator.init(
                KeyGenParameterSpec.Builder(
                    KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true) // Require that the user has unlocked in the last 30 seconds
                    .setUserAuthenticationValidityDurationSeconds(AUTHENTICATION_DURATION_SECONDS)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build()
            )
            keyGenerator.generateKey()
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to create a symmetric key", e)
        } catch (e: NoSuchProviderException) {
            throw RuntimeException("Failed to create a symmetric key", e)
        } catch (e: InvalidAlgorithmParameterException) {
            throw RuntimeException("Failed to create a symmetric key", e)
        } catch (e: KeyStoreException) {
            throw RuntimeException("Failed to create a symmetric key", e)
        } catch (e: CertificateException) {
            throw RuntimeException("Failed to create a symmetric key", e)
        } catch (e: IOException) {
            throw RuntimeException("Failed to create a symmetric key", e)
        }
    }

    fun showAuthenticationScreen() {
        lockScreenCallback.showAuthenticationScreen()
    }


    companion object {

        private const val KEY_NAME = "my_key"
        const val REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 1
        private const val AUTHENTICATION_DURATION_SECONDS = 30

        private val SECRET_BYTE_ARRAY = byteArrayOf(1, 2, 3, 4, 5, 6)

    }


}