package com.foxminded.android.locationtrackerkotlin.extensions

import android.text.TextUtils
import android.util.Patterns
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

fun String.isValidEmail() =
    !TextUtils.isEmpty(this) && Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.isValidPhone() =
    !TextUtils.isEmpty(this) && Patterns.PHONE.matcher(this).matches() && this.length >= 12

fun String.isValidSmsCode() =
    !TextUtils.isEmpty(this) && this.length == 6

fun String.isValidPassword() =
    !TextUtils.isEmpty(this) && this.length >= 6