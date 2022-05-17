package com.foxminded.android.trackerapp.maps

import com.foxminded.android.locationtrackerkotlin.firestoreuser.User

interface IMapsRepoFirestore {

    suspend fun insertDataToFirestore(user: User)

    suspend fun currentFirebaseUser(): String?

    suspend fun signOut(): Boolean

}