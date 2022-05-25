package com.foxminded.android.locationtrackerkotlin.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foxminded.android.locationtrackerkotlin.state.BaseViewState
import com.foxminded.android.locationtrackerkotlin.state.SignUpButtonState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val SIGN_UP = 1

class SignUpViewModel(
    private var signUpRepo: ISignUpRepo,
) : ViewModel() {

    private val TAG = SignUpViewModel::class.java.simpleName

    private val _viewState = MutableStateFlow<BaseViewState>(BaseViewState.DefaultState)
    val viewState: StateFlow<BaseViewState> = _viewState.asStateFlow()

    private val _buttonState = MutableStateFlow<SignUpButtonState>(SignUpButtonState.DefaultState)
    val buttonState: StateFlow<SignUpButtonState> = _buttonState.asStateFlow()

    private var email = MutableStateFlow("")
    private var password = MutableStateFlow("")
    private var passwordAgain = MutableStateFlow("")

    fun checkAllFieldsValue(email1: String?, password1: String?, passwordAgain1: String?) {
        email1.also {
            if (it != null) {
                this.email.value = it
            }
        }
        password1.also {
            if (it != null) {
                this.password.value = it
            }
        }
        passwordAgain1.also {
            if (it != null) {
                this.passwordAgain.value = it
            }
        }
        if (email.value.contains("@") && email.value.contains(".")
            && password.value.length >= 6 && passwordAgain.value == password.value
        ) {
            _buttonState.value = SignUpButtonState.IsButtonSignUpEnablerState(true)
        } else {
            _buttonState.value = SignUpButtonState.IsButtonSignUpEnablerState(false)
        }
    }

    fun createAccount() {
        _viewState.value = BaseViewState.LoadingState
        viewModelScope.launch(Dispatchers.IO) {
            signUpRepo.createAccountWithEmail(email.value, password.value)
            _viewState.value = BaseViewState.SuccessState(SIGN_UP,
                "Complete! Verification link sent to ${email.value}")
        }
    }
}