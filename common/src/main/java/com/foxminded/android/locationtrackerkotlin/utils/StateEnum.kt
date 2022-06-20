package com.foxminded.android.locationtrackerkotlin.utils

enum class StateEnum(val state: String) {
    DEFAULT("default"),
    SIGN_UP("signUp"),
    SIGN_IN("signIn"),
    SIGN_OUT("signOut"),
    ACCOUNT("account"),
    SEND_SMS("sendSms"),
    VERIFY_PHONE_WITH_CODE("verifyPhone"),
    SIGN_IN_WITH_CREDENTIAL("withCredential"),
    RESEND_CODE("resendCode"),
    FORGOT_PASSWORD("forgotPassword")
}