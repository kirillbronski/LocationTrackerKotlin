package com.foxminded.android.locationtrackerkotlin.phoneauth

import android.app.Activity
import android.util.Log
import com.foxminded.android.locationtrackerkotlin.utils.BaseResult
import com.foxminded.android.locationtrackerkotlin.utils.PhoneAuthResult
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val TIMEOUT = 60L

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
                .setTimeout(TIMEOUT, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(object :
                    PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                        Log.d(TAG, "onVerificationCompleted: $phoneAuthCredential")
                        it.resume(PhoneAuthResult.VerificationCompleted(phoneAuthCredential))
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        it.resume(PhoneAuthResult.Error(e.message))
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


    override suspend fun verifyPhoneNumberWithCode(code: String): PhoneAuthResult =

        runCatching {
            PhoneAuthProvider.getCredential(mVerificationId, code)
        }.fold(
            onSuccess = {
                PhoneAuthResult.VerificationCompleted(it)
            },
            onFailure = {
                PhoneAuthResult.Error(it.message)
            }
        )

    override suspend fun resendVerificationCode(
        phoneNumber: String,
        activity: Activity,
    ): PhoneAuthResult =
        suspendCoroutine {
            PhoneAuthProvider.verifyPhoneNumber(PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(TIMEOUT, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                        Log.d(TAG, "onVerificationCompleted: $phoneAuthCredential")
                        it.resume(PhoneAuthResult.VerificationCompleted(phoneAuthCredential))
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        it.resume(PhoneAuthResult.Error(e.message))
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

    override suspend fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential): BaseResult =
        runCatching {
            firebaseAuth.signInWithCredential(credential).await()
        }.fold(
            onSuccess = {
                BaseResult.Success(it.user?.phoneNumber)
            },
            onFailure = {
                Log.e(TAG, "signInWithPhoneAuthCredential: ${it.message}", it)
                BaseResult.Error(it.message)
            }
        )
}