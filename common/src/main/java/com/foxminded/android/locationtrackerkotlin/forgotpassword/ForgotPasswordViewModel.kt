package com.foxminded.android.locationtrackerkotlin.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foxminded.android.locationtrackerkotlin.state.BaseViewState
import com.foxminded.android.locationtrackerkotlin.state.ForgotPasswordButtonState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val FORGOT_PASSWORD = 1

class ForgotPasswordViewModel(
    private val forgotPasswordRepoImpl: IForgotPasswordRepo,
) : ViewModel() {

    private val _viewState = MutableStateFlow<BaseViewState>(BaseViewState.DefaultState)
    val viewState: StateFlow<BaseViewState> = _viewState.asStateFlow()

    private val _buttonState =
        MutableStateFlow<ForgotPasswordButtonState>(ForgotPasswordButtonState.DefaultState)
    val buttonState: StateFlow<ForgotPasswordButtonState> = _buttonState.asStateFlow()

    private val email = MutableStateFlow("")

    fun checkEmailField(emailFieldValue: String) {
        emailFieldValue.also {
            this.email.value = it
        }
        if (email.value.contains("@") && email.value.contains(".")) {
            _buttonState.value = ForgotPasswordButtonState.IsButtonResetPasswordEnablerState(true)
        } else {
            _buttonState.value = ForgotPasswordButtonState.IsButtonResetPasswordEnablerState(false)
        }
    }

    fun passwordReset() {
        _viewState.value = BaseViewState.LoadingState
        viewModelScope.launch(Dispatchers.IO) {
            if (forgotPasswordRepoImpl.sendPasswordReset(email.value)) {
                _viewState.value = BaseViewState.SuccessState(
                    FORGOT_PASSWORD,
                    stringValue = "Password reset link sent to: ${email.value}")
            } else {
                _viewState.value = BaseViewState.ErrorState("Failed! Details in logs")
            }
        }
    }

}