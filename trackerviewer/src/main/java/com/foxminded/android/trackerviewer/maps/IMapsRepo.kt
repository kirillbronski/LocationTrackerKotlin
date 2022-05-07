package com.foxminded.android.trackerviewer.maps

import com.foxminded.android.locationtrackerkotlin.firestoreuser.User

interface IMapsRepo {

    suspend fun getDataFromFirestore(): List<User>

    suspend fun signOut()

}