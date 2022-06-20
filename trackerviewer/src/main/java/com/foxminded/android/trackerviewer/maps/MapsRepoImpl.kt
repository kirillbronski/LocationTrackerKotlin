package com.foxminded.android.trackerviewer.maps

import android.util.Log
import com.foxminded.android.locationtrackerkotlin.firestoreuser.User
import com.foxminded.android.locationtrackerkotlin.utils.BaseResult
import com.foxminded.android.trackerviewer.utils.IConfigApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MapsRepoImpl(
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val configApp: IConfigApp,
) : IMapsRepo {

    private val TAG = MapsRepoImpl::class.java.simpleName

    override suspend fun getDataFromFirestore(): List<User>? {
        return try {
            val users = firebaseFirestore
                .collection(configApp.firestoreCollectionName())
                .get()
                .await()
                .toObjects(User::class.java)
            users
        } catch (e: Exception) {
            Log.e(TAG, "getDataFromFirestore: $e")
            null
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