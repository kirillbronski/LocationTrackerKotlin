package com.foxminded.android.trackerapp.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "account")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val uid: Long?,
    val accountInfo: String,
    val latitude: Double,
    val longitude: Double,
    val dateAndTime: String,
) {
//    constructor(
//        accountInfo: String,
//        latitude: Double,
//        longitude: Double,
//        dateAndTime: String,
//    ) : this(null, "", -1.0, -1.0, "")
}
