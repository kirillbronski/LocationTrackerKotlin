package com.foxminded.android.locationtrackerkotlin.phoneauth

import android.app.Activity
import com.foxminded.android.locationtrackerkotlin.utils.PhoneAuthResult
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

interface IPhoneAuthRepo {

    suspend fun verifyPhoneNumber(phoneNumber: String, activity: Activity): PhoneAuthResult

    suspend fun verifyPhoneNumberWithCode(code: String): PhoneAuthCredential?

    suspend fun resendVerificationCode(phoneNumber: String, activity: Activity): PhoneAuthResult

    suspend fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential): AuthResult?

}
