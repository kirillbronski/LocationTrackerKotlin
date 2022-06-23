package com.foxminded.android.locationtrackerkotlin.base

import com.foxminded.android.locationtrackerkotlin.utils.BaseResult
import com.google.firebase.auth.FirebaseAuth

open class BaseRepo(
    private var firebaseAuth: FirebaseAuth,
) : IBaseRepo {

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

    override suspend fun signOut(): BaseResult =
        runCatching {
            firebaseAuth.signOut()
        }.fold(
            onSuccess = {
                BaseResult.Success(null)
            },
            onFailure = {
                BaseResult.Error(it.message)
            })
}