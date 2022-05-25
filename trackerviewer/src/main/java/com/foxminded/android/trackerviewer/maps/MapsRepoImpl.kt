package com.foxminded.android.trackerviewer.maps

import android.util.Log
import com.foxminded.android.locationtrackerkotlin.firestoreuser.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

private const val COLLECTION_NAME = "LocationTrackerUsers"

class MapsRepoImpl(
    private val firebaseFirestore: FirebaseFirestore,
    private var firebaseAuth: FirebaseAuth,
) : IMapsRepo {

    private val TAG = MapsRepoImpl::class.java.simpleName

    override suspend fun getDataFromFirestore(): List<User>? {
        return try {
            val users = firebaseFirestore
                .collection(COLLECTION_NAME)
                .get()
                .await()
                .toObjects(User::class.java)
            users
        } catch (e: Exception) {
            Log.e(TAG, "getDataFromFirestore: $e")
            null
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