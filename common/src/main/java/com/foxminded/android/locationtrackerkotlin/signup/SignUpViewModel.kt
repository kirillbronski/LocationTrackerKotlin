package com.foxminded.android.locationtrackerkotlin.signup

import android.text.TextUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foxminded.android.locationtrackerkotlin.extensions.set
import com.foxminded.android.locationtrackerkotlin.state.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignUpViewModel(
    private var signUpRepo: ISignUpRepo,
) : ViewModel() {

    private val TAG = SignUpViewModel::class.java.simpleName

    private val _signUpState = MutableStateFlow<State>(State.DefaultState)
    val signUpState: StateFlow<State> = _signUpState.asStateFlow()

    private var email: String = ""
    private var password: String = ""
    private var passwordAgain: String = ""

    fun requestEmailFromUser(email: String): String {
        return email.also { this.email = it }
    }

    fun requestPasswordFromUser(password: String): String {
        return password.also { this.password = it }
    }

    fun requestPasswordAgainFromUser(passwordAgain: String): String {
        return passwordAgain.also { this.passwordAgain = it }
    }

    fun createAccount() {
        if (validateForm()) {
            _signUpState.set(State.ProgressIndicatorState)
            viewModelScope.launch {
                val result = signUpRepo.createAccountWithEmail(email, password)
                if (result != null) {
                    _signUpState.set(State.SucceededState)
                    _signUpState.set(State.AccountInfoState("Complete! Verification link sent to $email"))
                } else {
                    _signUpState.set(State.ErrorState("Sign Up Filed!"))
                }
            }
        }
    }

    private fun validateForm(): Boolean {
        var valid = true
        if (TextUtils.isEmpty(email) || !email.contains("@") || !email.contains(".")) {
            _signUpState.set(State.ErrorState("Invalid Email"))
            valid = false
        }
        if (TextUtils.isEmpty(password) || password.length < 6) {
            _signUpState.set(State.ErrorState("Invalid Password"))
            valid = false
        }
        if (TextUtils.isEmpty(passwordAgain) || passwordAgain != password) {
            _signUpState.set(State.ErrorState("Invalid Password Again"))
            valid = false
        }
        return valid
    }
}