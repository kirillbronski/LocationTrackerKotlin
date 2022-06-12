package com.foxminded.android.locationtrackerkotlin.signin

import com.foxminded.android.locationtrackerkotlin.utils.BaseResult

interface ISignInRepo {

    suspend fun signIn(email: String, password: String): BaseResult

    suspend fun currentFirebaseUser(): String?

}
