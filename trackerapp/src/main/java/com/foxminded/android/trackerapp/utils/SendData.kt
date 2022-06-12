package com.foxminded.android.trackerapp.utils

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.foxminded.android.locationtrackerkotlin.firestoreuser.User
import com.foxminded.android.trackerapp.di.config.App
import com.foxminded.android.trackerapp.maps.IMapsRepo
import com.foxminded.android.trackerapp.maps.IMapsRepoFirestore
import com.foxminded.android.trackerapp.room.AccountEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SendData(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    private val TAG = SendData::class.java.simpleName

    @Inject
    lateinit var mapsRepoImpl: IMapsRepo

    @Inject
    lateinit var mapsRepoFirestoreImpl: IMapsRepoFirestore

    init {
        (applicationContext as App).mainComponent.injectSendData(this)
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {

        return@withContext try {
            readAllDataFromRoom()
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "doWork: ${e.message}", e)
            Result.failure()
        }
    }

    private suspend fun readAllDataFromRoom() {

        val dataFromRoom = mapsRepoImpl.readAllDataFromDatabase()
        if (dataFromRoom.isNotEmpty()) {
            fromRoomToFireStore(dataFromRoom)
        }
    }

    private suspend fun fromRoomToFireStore(dataFromRoom: MutableList<AccountEntity>) {
        dataFromRoom.forEach {
            mapsRepoFirestoreImpl.insertDataToFirestore(User(
                it.accountInfo,
                it.latitude,
                it.longitude,
                it.dateAndTime
            ))
        }.run {
            mapsRepoImpl.deleteAllDataFromTable()
        }
    }
}
