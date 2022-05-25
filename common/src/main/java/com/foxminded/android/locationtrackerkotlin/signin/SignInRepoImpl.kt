package com.foxminded.android.locationtrackerkotlin.signin

import android.util.Log
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class SignInRepoImpl(
    private var firebaseAuth: FirebaseAuth,
) : ISignInRepo {

    private val TAG = SignInRepoImpl::class.java.simpleName

    override suspend fun signIn(email: String, password: String): AuthResult? {
        return runCatching {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
        }.onFailure {
            Log.e(TAG, "signIn: ${it.message}", it)
        }.getOrNull()
    }

    override suspend fun currentFirebaseUser(): String? {
        if (firebaseAuth.currentUser != null) {
            if (firebaseAuth.currentUser?.email != "") {
                return firebaseAuth.currentUser?.email.toString()
            } else if (firebaseAuth.currentUser?.phoneNumber != "") {
                return firebaseAuth.currentUser?.phoneNumber.toString()
            }
            return "No User!"
        } else {
            return firebaseAuth.currentUser
        }
    }
}