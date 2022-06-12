package com.foxminded.android.locationtrackerkotlin.forgotpassword

import android.util.Log
import com.foxminded.android.locationtrackerkotlin.utils.BaseResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class ForgotPasswordRepoImpl(
    private val firebaseAuth: FirebaseAuth,
) : IForgotPasswordRepo {

    private val TAG = ForgotPasswordRepoImpl::class.java.simpleName

    override suspend fun sendPasswordReset(email: String): BaseResult =
        runCatching {
            firebaseAuth.sendPasswordResetEmail(email).await()
        }.fold(
            onSuccess = {
                BaseResult.Success(email)
            },
            onFailure = {
                Log.e(TAG, "sendPasswordReset: ${it.message}")
                BaseResult.Error(it.message)
            }
        )

}