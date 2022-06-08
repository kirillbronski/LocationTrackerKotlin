package com.foxminded.android.locationtrackerkotlin.signup

import android.util.Log
import com.foxminded.android.locationtrackerkotlin.utils.BaseResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class SignUpRepoImpl(
    private var firebaseAuth: FirebaseAuth,
) : ISignUpRepo {

    private val TAG = SignUpRepoImpl::class.java.simpleName

    override suspend fun createAccountWithEmail(email: String, password: String): BaseResult =
        runCatching {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        }.fold(
            onSuccess = {
                sendEmailVerification(it.user)
                BaseResult.Success(it.user?.email.toString())
            },
            onFailure = {
                Log.e(TAG, "createAccountWithEmail: ${it.message}", it)
                BaseResult.Error(it.message)
            }
        )

    private suspend fun sendEmailVerification(user: FirebaseUser?) =
        runCatching {
            user?.sendEmailVerification()?.await()
        }.fold(
            onSuccess = {
                Log.e(TAG, "sendEmailVerification onSuccess: ${it.toString()}")
            },
            onFailure = {
                Log.e(TAG, "sendEmailVerification onFailure: ${it.message}", it)
            }
        )
}