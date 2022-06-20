package com.foxminded.android.locationtrackerkotlin.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foxminded.android.locationtrackerkotlin.extensions.isValidEmail
import com.foxminded.android.locationtrackerkotlin.extensions.isValidPassword
import com.foxminded.android.locationtrackerkotlin.state.ViewState
import com.foxminded.android.locationtrackerkotlin.utils.BaseResult
import com.foxminded.android.locationtrackerkotlin.utils.StateEnum.SIGN_UP
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignUpViewModel(
    private var signUpRepo: ISignUpRepo,
) : ViewModel() {

    private val TAG = SignUpViewModel::class.java.simpleName

    private val _viewState = MutableStateFlow<ViewState>(ViewState.DefaultState)
    val viewState: StateFlow<ViewState?> = _viewState.asStateFlow()

    private val _buttonState = MutableStateFlow(false)
    val buttonState: StateFlow<Boolean> = _buttonState.asStateFlow()

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
        _buttonState.value = (email.value.isValidEmail()
                && password.value.isValidPassword() && passwordAgain.value == password.value)
    }

    fun createAccount() {
        _viewState.value = ViewState.LoadingState
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.value =
                signUpRepo.createAccountWithEmail(email.value, password.value).run {
                    when (this) {
                        is BaseResult.Success -> {
                            ViewState.SuccessState(SIGN_UP.state,
                                "Complete! Verification link sent to ${this.successMessage}")
                        }
                        is BaseResult.Error -> {
                            Log.d(TAG, "createAccount: ${this.errorMessage}")
                            ViewState.ErrorState(this.errorMessage)
                        }
                    }
                }
        }
    }
}