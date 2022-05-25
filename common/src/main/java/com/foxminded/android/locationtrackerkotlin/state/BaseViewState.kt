package com.foxminded.android.locationtrackerkotlin.state

sealed class BaseViewState {
    object DefaultState : BaseViewState()
    object LoadingState : BaseViewState()
    class SuccessState(
        var state: Int?,
        val stringValue: String?,
    ) : BaseViewState()

    class ErrorState(var message: String?) : BaseViewState()
}
