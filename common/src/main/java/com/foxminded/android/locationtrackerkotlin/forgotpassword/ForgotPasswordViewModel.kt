package com.foxminded.android.locationtrackerkotlin.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foxminded.android.locationtrackerkotlin.extensions.isValidEmail
import com.foxminded.android.locationtrackerkotlin.state.BaseViewState
import com.foxminded.android.locationtrackerkotlin.utils.StateConst.FORGOT_PASSWORD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val forgotPasswordRepoImpl: IForgotPasswordRepo,
) : ViewModel() {

    private val _viewState = MutableStateFlow<BaseViewState>(BaseViewState.DefaultState)
    val viewState: StateFlow<BaseViewState> = _viewState.asStateFlow()

    private val _buttonState = MutableStateFlow(false)
    val buttonState: StateFlow<Boolean> = _buttonState.asStateFlow()

    private val email = MutableStateFlow("")

    fun checkEmailField(emailFieldValue: String) {
        emailFieldValue.also {
            this.email.value = it
        }
        _buttonState.value = email.value.isValidEmail()
    }

    fun passwordReset() {
        _viewState.value = BaseViewState.LoadingState
        viewModelScope.launch(Dispatchers.IO) {
            if (forgotPasswordRepoImpl.sendPasswordReset(email.value)) {
                _viewState.value = BaseViewState.SuccessState(
                    FORGOT_PASSWORD.state,
                    stringValue = "Password reset link sent to: ${email.value}")
            } else {
                _viewState.value = BaseViewState.ErrorState("Failed! Details in logs")
            }
        }
    }

}