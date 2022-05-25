package com.foxminded.android.locationtrackerkotlin.signin

import com.google.firebase.auth.AuthResult

interface ISignInRepo {

    suspend fun signIn(email: String, password: String): AuthResult?

    suspend fun currentFirebaseUser(): String?

}
