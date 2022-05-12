package com.foxminded.android.locationtrackerkotlin.phoneauth

import android.app.Activity
import android.util.Log
import com.foxminded.android.locationtrackerkotlin.utils.PhoneAuthResult
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class PhoneAuthRepoImpl(
    private var firebaseAuth: FirebaseAuth,
) : IPhoneAuthRepo {

    private val TAG = PhoneAuthRepoImpl::class.java.simpleName
    private lateinit var mVerificationId: String
    private lateinit var token: ForceResendingToken

    override suspend fun verifyPhoneNumber(
        phoneNumber: String,
        activity: Activity,
    ): PhoneAuthResult =
        suspendCoroutine {
            PhoneAuthProvider.verifyPhoneNumber(PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(object :
                    PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                        Log.d(TAG, "onVerificationCompleted: $phoneAuthCredential")
                        it.resume(PhoneAuthResult.VerificationCompleted(phoneAuthCredential))
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        it.resumeWithException(e)
                        Log.e(TAG, "onVerificationFailed: ", e)
                    }

                    override fun onCodeSent(
                        verificationId: String,
                        resendToken: ForceResendingToken,
                    ) {
                        super.onCodeSent(verificationId, resendToken)
                        mVerificationId = verificationId
                        token = resendToken
                        it.resume(PhoneAuthResult.CodeSent(verificationId, token))
                        Log.d(TAG, "onCodeSent: $verificationId $resendToken")
                    }
                }).build())
        }


    override suspend fun verifyPhoneNumberWithCode(code: String): PhoneAuthCredential? {
        return try {
            val credential = PhoneAuthProvider.getCredential(mVerificationId, code)
            credential
        } catch (e: Exception) {
            return null
        }
    }

    override suspend fun resendVerificationCode(
        phoneNumber: String,
        activity: Activity,
    ): PhoneAuthResult =
        suspendCoroutine {
            PhoneAuthProvider.verifyPhoneNumber(PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                        Log.d(TAG, "onVerificationCompleted: $phoneAuthCredential")
                        it.resume(PhoneAuthResult.VerificationCompleted(phoneAuthCredential))
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        it.resumeWithException(e)
                        Log.e(TAG, "onVerificationFailed resend: ", e)
                    }

                    override fun onCodeSent(
                        verificationId: String,
                        resendToken: ForceResendingToken,
                    ) {
                        super.onCodeSent(verificationId, resendToken)
                        mVerificationId = verificationId
                        token = resendToken
                        Log.d(TAG, "onCodeSent resend: $verificationId $resendToken")
                        it.resume(PhoneAuthResult.CodeSent(verificationId, token))

                    }
                })
                .setForceResendingToken(token)
                .build())
        }

    override suspend fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential): AuthResult? {
        return try {
            firebaseAuth.signInWithCredential(credential).await()
        } catch (e: Exception) {
            Log.e(TAG, "signInWithPhoneAuthCredential: $e", e)
            return null
        }
    }

}