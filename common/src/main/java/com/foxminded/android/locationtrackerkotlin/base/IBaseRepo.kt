package com.foxminded.android.locationtrackerkotlin.base

import com.foxminded.android.locationtrackerkotlin.utils.BaseResult

interface IBaseRepo {
    suspend fun currentFirebaseUser(): String?
    suspend fun signOut(): BaseResult
}