package com.foxminded.android.locationtrackerkotlin.signin

import android.app.Activity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser

interface ISignInRepo {

    suspend fun signIn(email: String, password: String): AuthResult?

    suspend fun sendPasswordReset(email: String): Boolean

    suspend fun signOut(): Boolean

    suspend fun currentFirebaseUser(): String?

}
