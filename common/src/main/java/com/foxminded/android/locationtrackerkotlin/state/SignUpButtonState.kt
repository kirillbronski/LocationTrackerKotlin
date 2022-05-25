package com.foxminded.android.locationtrackerkotlin.state

sealed class SignUpButtonState {
    object DefaultState : SignUpButtonState()
    class IsButtonSignUpEnablerState(val enabler: Boolean) : SignUpButtonState()
}
