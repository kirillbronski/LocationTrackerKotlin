package com.foxminded.android.locationtrackerkotlin.state

sealed class State {
    object DefaultState : State()
    object ProgressIndicatorState : State()
    object SucceededState : State()
    object ButtonTimer : State()
    class PasswordResetState(val email: String) : State()
    class SmsSendState(val value: String) : State()
    class AccountInfoState(val accountInfo: String) : State()
    class ErrorState(val message: String) : State()
}
