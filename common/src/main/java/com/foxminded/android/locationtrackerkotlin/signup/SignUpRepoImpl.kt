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

    override suspend fun createAccountWithEmail(email: String, password: String): AuthResult? {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            sendEmailVerification(result.user)
            result
        } catch (e: Exception) {
            Log.e(TAG, "createAccountWithEmail: $e")
            return null
        }
    }

    private suspend fun sendEmailVerification(user: FirebaseUser?) {
        try {
            user?.sendEmailVerification()?.await()
        } catch (e: Exception) {
            Log.e(TAG, "sendEmailVerification: $e")
        }
    }
}