package com.foxminded.android.trackerviewer.maps

import android.util.Log
import com.foxminded.android.locationtrackerkotlin.firestoreuser.User
import com.foxminded.android.locationtrackerkotlin.utils.COLLECTION_NAME
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow

class MapsRepoImpl(
    private val firebaseFirestore: FirebaseFirestore,
    private var firebaseAuth: FirebaseAuth,
) : IMapsRepo {

    private val TAG = MapsRepoImpl::class.java.simpleName
    private var users = mutableListOf<User>()

    override fun getDataFromFirestore(): Flow<List<User>> {
        return callbackFlow {
            firebaseFirestore
                .collection(COLLECTION_NAME)
                .get()
                .addOnCompleteListener { task: Task<QuerySnapshot> ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            users.add(document.toObject(User::class.java))
                        }
                        trySend(users)
                    } else {
                        close(task.exception)
                        Log.d(TAG, "Error getting documents: ", task.exception)
                    }
                }.addOnFailureListener {
                    it.message
                    Log.d(TAG, "Error getting documents: ", it)
                }
            awaitClose()
        }
    }

    override suspend fun signOut() {
        if (firebaseAuth.currentUser != null) {
            firebaseAuth.signOut()
        }
    }
}