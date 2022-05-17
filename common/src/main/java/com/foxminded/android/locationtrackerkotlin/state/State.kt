package com.foxminded.android.locationtrackerkotlin.state

import com.google.android.gms.maps.model.MarkerOptions

sealed class State {
    object DefaultState : State()
    object ProgressIndicatorState : State()
    object SucceededState : State()
    object ButtonTimer : State()
    object SignOut : State()
    class PasswordResetState(val email: String) : State()
    class SmsSendState(val value: String) : State()
    class AccountInfoState(val accountInfo: String) : State()
    class ErrorState(val message: String) : State()
    class StateLocationListener(val message: String) : State()
    class MarkerState(val markers: MutableCollection<MarkerOptions>) : State()
}
