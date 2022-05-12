package com.foxminded.android.locationtrackerkotlin.extensions

import com.foxminded.android.locationtrackerkotlin.firestoreuser.User
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.flow.MutableStateFlow

fun User.buildMarkers(
    latitude: Double, longitude: Double,
    accountInfo: String?, dateAndTime: String?,
): MarkerOptions {
    return MarkerOptions()
        .position(LatLng(latitude, longitude))
        .title(accountInfo)
        .snippet(dateAndTime)
}

fun <T> MutableStateFlow<T>.set(newValue: T) = apply { value = newValue }