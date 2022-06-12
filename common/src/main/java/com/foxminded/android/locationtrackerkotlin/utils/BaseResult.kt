package com.foxminded.android.locationtrackerkotlin.utils

sealed class BaseResult {
    class Success(val successMessage: String?) : BaseResult()
    class Error(val errorMessage: String?) : BaseResult()
}
