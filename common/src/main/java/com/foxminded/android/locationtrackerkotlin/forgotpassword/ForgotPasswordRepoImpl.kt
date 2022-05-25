package com.foxminded.android.locationtrackerkotlin.forgotpassword

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class ForgotPasswordRepoImpl(
    private val firebaseAuth: FirebaseAuth,
) : IForgotPasswordRepo {

    private val TAG = ForgotPasswordRepoImpl::class.java.simpleName

    override suspend fun sendPasswordReset(email: String): Boolean {
        return runCatching {
            firebaseAuth.sendPasswordResetEmail(email).await()
        }.onFailure {
            Log.e(TAG, "sendPasswordReset: ${it.message}")
        }.isSuccess
    }

}