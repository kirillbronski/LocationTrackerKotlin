package com.foxminded.android.trackerviewer.extensions

import com.foxminded.android.locationtrackerkotlin.firestoreuser.User
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

fun User.buildMarkers(
    latitude: Double, longitude: Double,
    accountInfo: String?, dateAndTime: String?,
): MarkerOptions {
    return MarkerOptions()
        .position(LatLng(latitude, longitude))
        .title(accountInfo)
        .snippet(dateAndTime)
}