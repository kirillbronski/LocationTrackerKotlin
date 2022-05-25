package com.foxminded.android.locationtrackerkotlin.firestoreuser

data class User(
    var accountInfo: String?,
    var latitude: Double,
    var longitude: Double,
    var dateAndTime: String?,
) {
    constructor() : this(
        accountInfo = null,
        latitude = -1.0,
        longitude = -1.0,
        dateAndTime = null)
}