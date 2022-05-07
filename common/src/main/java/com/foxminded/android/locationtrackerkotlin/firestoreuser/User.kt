package com.foxminded.android.locationtrackerkotlin.firestoreuser

data class User(
    var accountInfo: String?,
    var latitude: Double,
    var longitude: Double,
    var dateAndTime: String?,
) {

    constructor() : this(null,
        -1.0,
        -1.0,
        null)
}