package com.foxminded.android.trackerapp.maps

import android.util.Log
import com.foxminded.android.locationtrackerkotlin.firestoreuser.User
import com.foxminded.android.locationtrackerkotlin.utils.BaseResult
import com.foxminded.android.trackerapp.utils.IConfigApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

private const val ACCOUNT_INFO = "accountInfo"
private const val LATITUDE = "latitude"
private const val LONGITUDE = "longitude"
private const val DATE_AND_TIME = "dateAndTime"

class MapsRepoFirestoreImpl(
    private val database: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val configApp: IConfigApp,
) : IMapsRepoFirestore {

    private val TAG = MapsRepoFirestoreImpl::class.java.simpleName

    override suspend fun insertDataToFirestore(user: User) {
        try {
            database.collection(configApp.firestoreCollectionName()).add(addEntityData(user))
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "insertDataToFirestore: ${e.message}", e)
        }
    }

    private fun addEntityData(entity: User): Map<String, Any> {
        val user: MutableMap<String, Any> = HashMap()
        user[ACCOUNT_INFO] = entity.accountInfo!!
        user[LATITUDE] = entity.latitude
        user[LONGITUDE] = entity.longitude
        user[DATE_AND_TIME] = entity.dateAndTime!!
        return user
    }

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