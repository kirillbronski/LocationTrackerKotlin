package com.foxminded.android.locationtrackerkotlin.accountinfo

interface IAccountInfoRepo {

    suspend fun currentFirebaseUser(): String?

    suspend fun signOut(): Boolean
}
