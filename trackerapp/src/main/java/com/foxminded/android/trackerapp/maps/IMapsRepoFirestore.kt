package com.foxminded.android.trackerapp.maps

import com.foxminded.android.locationtrackerkotlin.firestoreuser.User
import com.foxminded.android.locationtrackerkotlin.utils.BaseResult

interface IMapsRepoFirestore {

    suspend fun insertDataToFirestore(user: User)

    suspend fun currentFirebaseUser(): String?

    suspend fun signOut(): BaseResult

}