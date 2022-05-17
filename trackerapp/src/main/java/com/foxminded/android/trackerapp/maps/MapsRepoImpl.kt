package com.foxminded.android.trackerapp.maps

import android.util.Log
import com.foxminded.android.trackerapp.room.AccountDao
import com.foxminded.android.trackerapp.room.AccountEntity

class MapsRepoImpl(
    private val accountDao: AccountDao,
) : IMapsRepo {

    private val TAG = MapsRepoImpl::class.java.simpleName

    override suspend fun insertInTable(
        accountInfo: String,
        latitude: Double,
        longitude: Double,
        dateAndTime: String,
    ) {
        try {
            accountDao.insert(AccountEntity(null, accountInfo, latitude, longitude, dateAndTime))
        } catch (e: Exception) {
            Log.e(TAG, "insertInTable: ${e.message}", e)
        }
    }

    override suspend fun readAllDataFromDatabase(): MutableList<AccountEntity> {
        return try {
            val result = accountDao.getAll()
            result
        } catch (e: Exception) {
            Log.e(TAG, "readAllDataFromDatabase: ${e.message}", e)
            return mutableListOf()
        }
    }

    override suspend fun deleteAllDataFromTable(): Int? {
        return try {
            val result = accountDao.deleteAll()
            result
        } catch (e: Exception) {
            Log.e(TAG, "deleteAllDataFromTable: ${e.message}", e)
        }
    }
}