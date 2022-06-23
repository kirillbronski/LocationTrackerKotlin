package com.foxminded.android.trackerviewer.maps

import com.foxminded.android.locationtrackerkotlin.base.IBaseRepo
import com.foxminded.android.locationtrackerkotlin.firestoreuser.User
import com.foxminded.android.locationtrackerkotlin.utils.BaseResult

interface IMapsRepo: IBaseRepo {
    suspend fun getDataFromFirestore(): List<User>?
}