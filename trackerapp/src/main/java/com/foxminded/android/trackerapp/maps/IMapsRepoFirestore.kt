package com.foxminded.android.trackerapp.maps

import com.foxminded.android.locationtrackerkotlin.base.IBaseRepo
import com.foxminded.android.locationtrackerkotlin.firestoreuser.User

interface IMapsRepoFirestore : IBaseRepo {

    suspend fun insertDataToFirestore(user: User)

}