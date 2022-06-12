package com.foxminded.android.locationtrackerkotlin.utils

import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

sealed class PhoneAuthResult {
    class VerificationCompleted(val credentials: PhoneAuthCredential) : PhoneAuthResult()

    class CodeSent(
        val verificationId: String,
        val token: PhoneAuthProvider.ForceResendingToken,
    ) : PhoneAuthResult()

    class Error(val errorMessage: String?) : PhoneAuthResult()
}
