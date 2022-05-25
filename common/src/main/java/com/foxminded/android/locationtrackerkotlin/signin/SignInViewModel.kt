package com.foxminded.android.locationtrackerkotlin.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foxminded.android.locationtrackerkotlin.state.BaseViewState
import com.foxminded.android.locationtrackerkotlin.state.SignInButtonState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val SIGN_IN = 1
private const val ACCOUNT = 2

class SignInViewModel(
    private var signInRepo: ISignInRepo,
) : ViewModel() {

    private val TAG = SignInViewModel::class.java.simpleName

    private val _viewState = MutableStateFlow<BaseViewState>(BaseViewState.DefaultState)
    val viewState: StateFlow<BaseViewState> = _viewState.asStateFlow()

    private val _signInButtonState =
        MutableStateFlow<SignInButtonState>(SignInButtonState.DefaultState)
    val signInButtonState: StateFlow<SignInButtonState> = _signInButtonState.asStateFlow()

    private var email = MutableStateFlow("")
    private var password = MutableStateFlow("")

    fun checkEmailAndPasswordFieldsValue(
        email1: String?,
        password1: String?,
    ) {
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

        if (email.value.contains("@") && email.value.contains(".")
            && password.value.length >= 6
        ) {
            _signInButtonState.value = SignInButtonState.IsButtonSignInEnablerState(true)
        } else {
            _signInButtonState.value = SignInButtonState.IsButtonSignInEnablerState(false)
        }
    }

    fun signIn() {
        _viewState.value = BaseViewState.LoadingState
        viewModelScope.launch(Dispatchers.IO) {
            if (signInRepo.signIn(email.value, password.value) != null) {
                _viewState.value = BaseViewState.SuccessState(SIGN_IN, "Sign in Success!")
            } else {
                _viewState.value = BaseViewState.ErrorState("Failed! Details in logs")
            }
        }
    }

    fun requestAccountInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            val userInfo = signInRepo.currentFirebaseUser()
            if (userInfo != null) {
                _viewState.value =
                    BaseViewState.SuccessState(state = ACCOUNT, stringValue = userInfo)
            } else {
                _viewState.value = BaseViewState.ErrorState("Please sign in or sign up")
            }
        }
    }
}