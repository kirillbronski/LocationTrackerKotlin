package com.foxminded.android.locationtrackerkotlin.forgotpassword

import com.foxminded.android.locationtrackerkotlin.utils.BaseResult

interface IForgotPasswordRepo {

    suspend fun sendPasswordReset(email: String): BaseResult

}
