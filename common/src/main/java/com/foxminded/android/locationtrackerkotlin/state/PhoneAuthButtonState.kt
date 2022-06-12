package com.foxminded.android.locationtrackerkotlin.state

sealed class PhoneAuthButtonState {
    object DefaultState : PhoneAuthButtonState()
    class IsButtonSendEnablerState(val enabler: Boolean) : PhoneAuthButtonState()
    class IsButtonVerifyEnablerState(val enabler: Boolean) : PhoneAuthButtonState()
    class TimeButtonState<T>(val text: T) : PhoneAuthButtonState()
    class ErrorState(val message: String) : PhoneAuthButtonState()
}
