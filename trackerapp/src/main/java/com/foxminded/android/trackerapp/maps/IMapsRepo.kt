package com.foxminded.android.trackerapp.maps

import com.foxminded.android.trackerapp.room.AccountEntity

interface IMapsRepo {

    suspend fun insertInTable(
        accountInfo: String,
        latitude: Double,
        longitude: Double,
        dateAndTime: String,
    )

    suspend fun readAllDataFromDatabase(): MutableList<AccountEntity>

    suspend fun deleteAllDataFromTable(): Int?

}