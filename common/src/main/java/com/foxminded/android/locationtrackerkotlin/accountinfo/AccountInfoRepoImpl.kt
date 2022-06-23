package com.foxminded.android.locationtrackerkotlin.accountinfo

import com.foxminded.android.locationtrackerkotlin.base.BaseRepo
import com.google.firebase.auth.FirebaseAuth

class AccountInfoRepoImpl(
    firebaseAuth: FirebaseAuth,
) : BaseRepo(firebaseAuth), IAccountInfoRepo {
}