package com.foxminded.android.trackerviewer.maps

import com.foxminded.android.locationtrackerkotlin.base.IBaseRepo
import com.foxminded.android.locationtrackerkotlin.firestoreuser.User

interface IMapsRepo : IBaseRepo {
    suspend fun getDataFromFirestore(): List<User>?
}