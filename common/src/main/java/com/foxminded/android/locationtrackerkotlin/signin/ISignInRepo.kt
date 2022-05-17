package com.foxminded.android.locationtrackerkotlin.signin

import com.google.firebase.auth.AuthResult

interface ISignInRepo {

    suspend fun signIn(email: String, password: String): AuthResult?

    suspend fun sendPasswordReset(email: String): Boolean

    suspend fun signOut(): Boolean

    suspend fun currentFirebaseUser(): String?

}
