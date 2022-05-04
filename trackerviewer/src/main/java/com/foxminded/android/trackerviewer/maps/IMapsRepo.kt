package com.foxminded.android.trackerviewer.maps

import com.foxminded.android.locationtrackerkotlin.firestoreuser.User
import kotlinx.coroutines.flow.Flow

interface IMapsRepo {

    fun getDataFromFirestore(): Flow<List<User>>

    suspend fun signOut()

}