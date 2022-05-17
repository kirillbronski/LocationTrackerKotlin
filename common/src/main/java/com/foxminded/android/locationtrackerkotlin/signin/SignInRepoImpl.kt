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
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
        } catch (e: Exception) {
            Log.e(TAG, "signIn: $e")
            return null
        }
    }

    override suspend fun sendPasswordReset(email: String): Boolean {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "sendPasswordReset: $e")
            false
        }
    }

    override suspend fun signOut(): Boolean {
        firebaseAuth.signOut()
        if (firebaseAuth.currentUser == null) {
            return true
        }
        return false
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
            return null
        }
    }
}