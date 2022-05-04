package com.foxminded.android.locationtrackerkotlin.firestoreuser

class User {

    var accountInfo: String? = null
    var latitude = 0.0
    var longitude = 0.0
    var dateAndTime: String? = null

    constructor() {}

    constructor(accountInfo: String?, latitude: Double, longitude: Double, dateAndTime: String?) {
        this.accountInfo = accountInfo
        this.latitude = latitude
        this.longitude = longitude
        this.dateAndTime = dateAndTime
    }
}