package com.foxminded.android.locationtrackerkotlin.state

sealed class ForgotPasswordButtonState {
    object DefaultState : ForgotPasswordButtonState()
    class IsButtonResetPasswordEnablerState(val enabler: Boolean) : ForgotPasswordButtonState()
}