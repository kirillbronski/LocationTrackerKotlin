package com.foxminded.android.locationtrackerkotlin.utils

enum class StateConst(val state: Int) {
    DEFAULT(0),
    SIGN_UP(1),
    SIGN_IN(1),
    SIGN_OUT(3),
    ACCOUNT(2),
    SEND_SMS(1),
    VERIFY_PHONE_WITH_CODE(2),
    SIGN_IN_WITH_CREDENTIAL(3),
    RESEND_CODE(4),
    FORGOT_PASSWORD(1)
}