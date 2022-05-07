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

    //                .addOnCompleteListener { task: Task<QuerySnapshot> ->
//                    if (task.isSuccessful) {
//                        for (document in task.result) {
//                            users.add(document.toObject(User::class.java))
//                        }
//                        trySend(users)
//                    } else {
//                        close(task.exception)
//                        Log.d(TAG, "Error getting documents: ", task.exception)
//                    }
//                }.addOnFailureListener {
//                    it.message
//                    Log.d(TAG, "Error getting documents: ", it)
//                }.await()

    override suspend fun signOut() {
        if (firebaseAuth.currentUser != null) {
            firebaseAuth.signOut()
        }
    }
}