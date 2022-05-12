package com.foxminded.android.locationtrackerkotlin.signin

import android.text.TextUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foxminded.android.locationtrackerkotlin.extensions.set
import com.foxminded.android.locationtrackerkotlin.state.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignInViewModel(
    private var signInRepo: ISignInRepo,
) : ViewModel() {

    private val TAG = SignInViewModel::class.java.simpleName

    private val _signInState = MutableStateFlow<State>(State.DefaultState)
    val signInState: StateFlow<State> = _signInState.asStateFlow()

    private var email: String = ""
    private var password: String = ""

    fun requestEmailFromUser(email: String): String {
        return email.also { this.email = it }
    }

    fun requestPasswordFromUser(password: String): String {
        return password.also { this.password = it }
    }

    fun signIn() {
        if (validateForm()) {
            _signInState.set(State.ProgressIndicatorState)
            viewModelScope.launch {
                if (signInRepo.signIn(email, password) != null) {
                    _signInState.set(State.SucceededState)
                } else {
                    _signInState.set(State.ErrorState("Failed! Details in logs"))
                }
            }
        }
    }

    fun passwordReset() {
        if (isValidEmail()) {
            _signInState.set(State.ProgressIndicatorState)
            viewModelScope.launch {
                if (signInRepo.sendPasswordReset(email)) {
                    _signInState.set(State.PasswordResetState(email))
                } else {
                    _signInState.set(State.ErrorState("Failed! Details in logs"))
                }
            }
        }
    }

    fun requestAccountInfo() {
        viewModelScope.launch {
            val userInfo = signInRepo.currentFirebaseUser()
            if (userInfo != null) {
                _signInState.set(State.AccountInfoState(userInfo))
            } else {
                _signInState.set(State.ErrorState("Please sign in or sign up"))
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            if (signInRepo.signOut()) {
                _signInState.set(State.DefaultState)
            }
        }
    }

    private fun validateForm(): Boolean {
        isValidEmail()
        return isValidPassword()
    }

    private fun isValidPassword(): Boolean {
        if (TextUtils.isEmpty(password) && password.length < 6) {
            _signInState.set(State.ErrorState("Invalid Password"))
            return false
        }
        return true
    }

    private fun isValidEmail(): Boolean {
        if (email.isNotEmpty() && email.contains("@")
            && email.contains(".")
        ) {
            return true
        }
        _signInState.set(State.ErrorState("Invalid Email"))
        return false
    }
}