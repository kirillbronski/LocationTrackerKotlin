package com.foxminded.android.locationtrackerkotlin.phoneauth

import android.app.Activity
import com.foxminded.android.locationtrackerkotlin.utils.BaseResult
import com.foxminded.android.locationtrackerkotlin.utils.PhoneAuthResult
import com.google.firebase.auth.PhoneAuthCredential

interface IPhoneAuthRepo {

    suspend fun verifyPhoneNumber(phoneNumber: String, activity: Activity): PhoneAuthResult

    suspend fun verifyPhoneNumberWithCode(code: String): PhoneAuthResult

    suspend fun resendVerificationCode(phoneNumber: String, activity: Activity): PhoneAuthResult

    suspend fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential): BaseResult

}
