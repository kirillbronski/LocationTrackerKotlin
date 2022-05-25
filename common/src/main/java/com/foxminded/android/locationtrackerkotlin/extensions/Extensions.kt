package com.foxminded.android.locationtrackerkotlin.extensions

import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import com.foxminded.android.locationtrackerkotlin.firestoreuser.User
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun buildMarker(user: User): MarkerOptions {
    return MarkerOptions()
        .position(LatLng(user.latitude, user.longitude))
        .title(user.accountInfo)
        .snippet(user.dateAndTime)
}

fun textFieldListener(editText: EditText): Flow<String> {
    return callbackFlow {
        editText.addTextChangedListener {
            trySend(it.toString())
        }
        awaitClose()
    }
}