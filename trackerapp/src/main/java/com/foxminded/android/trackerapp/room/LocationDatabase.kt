package com.foxminded.android.trackerapp.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [AccountEntity::class], version = 1)
abstract class LocationDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
}