package com.foxminded.android.locationtrackerkotlin.state

sealed class SignInButtonState {
    object DefaultState : SignInButtonState()
    class IsButtonSignInEnablerState(val enabler: Boolean) : SignInButtonState()
    class IsButtonResetPasswordEnablerState(val enabler: Boolean) : SignInButtonState()
}
