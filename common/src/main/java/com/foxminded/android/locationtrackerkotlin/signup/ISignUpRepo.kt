package com.foxminded.android.locationtrackerkotlin.signup

import com.google.firebase.auth.AuthResult

interface ISignUpRepo {

    suspend fun createAccountWithEmail(email: String, password: String): AuthResult?

}
