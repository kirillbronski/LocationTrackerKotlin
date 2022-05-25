package com.foxminded.android.locationtrackerkotlin.state

sealed class PhoneAuthButtonState {

    object DefaultState : PhoneAuthButtonState()
    object ProgressIndicatorState : PhoneAuthButtonState()
    object SucceededState : PhoneAuthButtonState()

    class IsButtonSendEnablerState(val enabler: Boolean) : PhoneAuthButtonState()
    class IsButtonVerifyEnablerState(val enabler: Boolean) : PhoneAuthButtonState()
    class SmsSendState(val value: String) : PhoneAuthButtonState()
    class TimeButtonState<T>(val text: T) : PhoneAuthButtonState()
    class ErrorState(val message: String) : PhoneAuthButtonState() {

    }

}
