package com.foxminded.android.locationtrackerkotlin.signin

import android.util.Log
import com.foxminded.android.locationtrackerkotlin.base.BaseRepo
import com.foxminded.android.locationtrackerkotlin.utils.BaseResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class SignInRepoImpl(
    private var firebaseAuth: FirebaseAuth,
) : BaseRepo(firebaseAuth), ISignInRepo {

    private val TAG = SignInRepoImpl::class.java.simpleName

    override suspend fun signIn(email: String, password: String): BaseResult =
        runCatching {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
        }.fold(
            onSuccess = {
                BaseResult.Success("Sign in Success!")
            },
            onFailure = {
                Log.e(TAG, "signIn: ${it.message}", it)
                BaseResult.Error(it.message)
            }
        )
}