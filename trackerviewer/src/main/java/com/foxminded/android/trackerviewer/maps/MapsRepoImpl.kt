package com.foxminded.android.trackerviewer.maps

import com.foxminded.android.locationtrackerkotlin.firestoreuser.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

const val COLLECTION_NAME = "LocationTrackerUsers"

class MapsRepoImpl(
    private val firebaseFirestore: FirebaseFirestore,
    private var firebaseAuth: FirebaseAuth,
) : IMapsRepo {

    private val TAG = MapsRepoImpl::class.java.simpleName

    override suspend fun getDataFromFirestore(): List<User> {
        val users = mutableListOf<User>()
        firebaseFirestore
            .collection(COLLECTION_NAME)
            .get()
            .await()
            .toObjects(User::class.java)
            .forEach {
                users.add(it)
            }
        return users
    }

    override suspend fun signOut() {
        if (firebaseAuth.currentUser != null) {
            firebaseAuth.signOut()
        }
    }
}