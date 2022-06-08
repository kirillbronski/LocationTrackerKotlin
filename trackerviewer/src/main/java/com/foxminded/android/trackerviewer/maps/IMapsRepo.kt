package com.foxminded.android.trackerviewer.maps

import com.foxminded.android.locationtrackerkotlin.firestoreuser.User
import com.foxminded.android.locationtrackerkotlin.utils.BaseResult

interface IMapsRepo {

    suspend fun getDataFromFirestore(): List<User>?

    suspend fun signOut(): BaseResult

}