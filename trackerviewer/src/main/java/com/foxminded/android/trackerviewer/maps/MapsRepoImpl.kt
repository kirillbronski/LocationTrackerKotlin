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

    override suspend fun getDataFromFirestore(): List<User> {
        val users = mutableListOf<User>()
        return try {
            firebaseFirestore
                .collection(COLLECTION_NAME)
                .get()
                .await()
                .toObjects(User::class.java)
                .forEach {
                    users.add(it)
                }
            users
        } catch (e: Exception) {
            users.clear()
            Log.e(TAG, "getDataFromFirestore: $e")
            users
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