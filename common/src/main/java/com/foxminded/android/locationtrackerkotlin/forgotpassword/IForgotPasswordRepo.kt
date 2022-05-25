package com.foxminded.android.locationtrackerkotlin.forgotpassword

interface IForgotPasswordRepo {

    suspend fun sendPasswordReset(email: String): Boolean

}
