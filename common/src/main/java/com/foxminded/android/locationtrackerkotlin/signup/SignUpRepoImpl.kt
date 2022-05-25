package com.foxminded.android.locationtrackerkotlin.signup

import android.util.Log
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class SignUpRepoImpl(
    private var firebaseAuth: FirebaseAuth,
) : ISignUpRepo {

    private val TAG = SignUpRepoImpl::class.java.simpleName

    override suspend fun createAccountWithEmail(email: String, password: String): AuthResult {
        return runCatching {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        }.onSuccess {
            sendEmailVerification(it.user)
        }.onFailure {
            Log.e(TAG, "createAccountWithEmail: ${it.message}", it)
        }.getOrThrow()
    }

    private suspend fun sendEmailVerification(user: FirebaseUser?) {
        runCatching {
            user?.sendEmailVerification()?.await()
        }.onFailure {
            Log.e(TAG, "sendEmailVerification: ${it.message}", it)
        }
    }
}