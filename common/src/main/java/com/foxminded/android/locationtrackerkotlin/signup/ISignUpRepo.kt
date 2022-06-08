package com.foxminded.android.locationtrackerkotlin.signup

import com.foxminded.android.locationtrackerkotlin.utils.BaseResult

interface ISignUpRepo {

    suspend fun createAccountWithEmail(email: String, password: String): BaseResult

}
