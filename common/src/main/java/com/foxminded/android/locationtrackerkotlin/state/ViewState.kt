package com.foxminded.android.locationtrackerkotlin.state

sealed class ViewState {
    object DefaultState : ViewState()
    object LoadingState : ViewState()
    class SuccessState(
        var state: Int?,
        val stringValue: String?,
    ) : ViewState()

    class ErrorState(var message: String?) : ViewState()
}
