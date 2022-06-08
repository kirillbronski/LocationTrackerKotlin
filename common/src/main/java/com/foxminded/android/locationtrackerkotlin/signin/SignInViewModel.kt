package com.foxminded.android.locationtrackerkotlin.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foxminded.android.locationtrackerkotlin.extensions.isValidEmail
import com.foxminded.android.locationtrackerkotlin.extensions.isValidPassword
import com.foxminded.android.locationtrackerkotlin.state.BaseViewState
import com.foxminded.android.locationtrackerkotlin.utils.BaseResult
import com.foxminded.android.locationtrackerkotlin.utils.StateConst.ACCOUNT
import com.foxminded.android.locationtrackerkotlin.utils.StateConst.SIGN_IN
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignInViewModel(
    private var signInRepo: ISignInRepo,
) : ViewModel() {

    private val TAG = SignInViewModel::class.java.simpleName

    private val _viewState = MutableStateFlow<BaseViewState>(BaseViewState.DefaultState)
    val viewState: StateFlow<BaseViewState> = _viewState.asStateFlow()

    private val _signInButtonState = MutableStateFlow(false)
    val signInButtonState: StateFlow<Boolean> = _signInButtonState.asStateFlow()

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

        _signInButtonState.value = (email.value.isValidEmail()
                && password.value.isValidPassword())
    }

    fun signIn() {
        _viewState.value = BaseViewState.LoadingState
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.value = signInRepo.signIn(email.value, password.value).run {
                when (this) {
                    is BaseResult.Success -> {
                        BaseViewState.SuccessState(SIGN_IN.state, this.successMessage)
                    }
                    is BaseResult.Error -> {
                        BaseViewState.ErrorState(this.errorMessage)
                    }
                }
            }
        }
    }

    fun requestAccountInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            signInRepo.currentFirebaseUser().run {
                _viewState.value = if (this != null) {
                    BaseViewState.SuccessState(state = ACCOUNT.state,
                        stringValue = this)
                } else {
                    BaseViewState.ErrorState("Please sign in or sign up")
                }
            }
        }
    }
}
