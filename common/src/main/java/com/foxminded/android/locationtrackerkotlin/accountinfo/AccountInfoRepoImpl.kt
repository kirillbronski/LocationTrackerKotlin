package com.foxminded.android.locationtrackerkotlin.accountinfo

import com.google.firebase.auth.FirebaseAuth

class AccountInfoRepoImpl(
    private val firebaseAuth: FirebaseAuth,
) : IAccountInfoRepo {

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

    override suspend fun signOut(): Boolean {
        firebaseAuth.signOut()
        if (firebaseAuth.currentUser == null) {
            return true
        }
        return false
    }

}