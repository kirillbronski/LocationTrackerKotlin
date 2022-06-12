package com.foxminded.android.locationtrackerkotlin.phoneauth

import android.app.Activity
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foxminded.android.locationtrackerkotlin.extensions.isValidPhone
import com.foxminded.android.locationtrackerkotlin.extensions.isValidSmsCode
import com.foxminded.android.locationtrackerkotlin.state.ViewState
import com.foxminded.android.locationtrackerkotlin.state.PhoneAuthButtonState
import com.foxminded.android.locationtrackerkotlin.utils.BaseResult
import com.foxminded.android.locationtrackerkotlin.utils.PhoneAuthResult
import com.foxminded.android.locationtrackerkotlin.utils.StateConst.*
import com.google.firebase.auth.PhoneAuthCredential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val MILLIS_IN_FUTURE = 60000L
private const val COUNT_DOWN_INTERVAL = 1000L
private const val MILLIS_TO_SECONDS = 1000L

class PhoneAuthViewModel(
    private var phoneAuthRepo: IPhoneAuthRepo,
) : ViewModel() {

    private val TAG = PhoneAuthViewModel::class.java.simpleName

    private var phoneNumber = MutableStateFlow("")
    private var smsCode = MutableStateFlow("")

    private val _viewState = MutableStateFlow<ViewState>(ViewState.DefaultState)
    val viewState: StateFlow<ViewState> = _viewState.asStateFlow()
    private val _buttonState =
        MutableStateFlow<PhoneAuthButtonState>(PhoneAuthButtonState.DefaultState)
    val buttonState: StateFlow<PhoneAuthButtonState> = _buttonState.asStateFlow()

    fun checkPhoneField(phoneNumber: String): String {
        _buttonState.value = if (phoneNumber.isValidPhone()) {
            PhoneAuthButtonState.IsButtonSendEnablerState(true)
        } else {
            PhoneAuthButtonState.IsButtonSendEnablerState(false)
        }
        return phoneNumber.also { this.phoneNumber.value = it }
    }

    fun checkSmsCodeField(smsCode: String): String {
        _buttonState.value = if (smsCode.isValidSmsCode()) {
            PhoneAuthButtonState.IsButtonVerifyEnablerState(true)
        } else {
            PhoneAuthButtonState.IsButtonVerifyEnablerState(false)
        }
        return smsCode.also { this.smsCode.value = it }
    }

    fun verifyPhoneNumber(activity: Activity) {
        _viewState.value = ViewState.LoadingState
        startButtonTimer()
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.value =
                phoneAuthRepo.verifyPhoneNumber(phoneNumber.value, activity).run {
                    when (this) {
                        is PhoneAuthResult.CodeSent -> {
                            Log.d(TAG, "verifyPhoneNumberViewModel: $this")
                            ViewState.SuccessState(SEND_SMS.state,
                                "SMS send to number: ${phoneNumber.value}")
                        }
                        is PhoneAuthResult.Error -> {
                            Log.e(TAG, "${this.errorMessage}")
                            ViewState.ErrorState(this.errorMessage)
                        }
                        else -> {
                            ViewState.DefaultState
                        }
                    }
                }
        }
    }

    fun verifyPhoneNumberWithCode() {
        _viewState.value = ViewState.LoadingState
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.value =
                phoneAuthRepo.verifyPhoneNumberWithCode(smsCode.value).run {
                    when (this) {
                        is PhoneAuthResult.VerificationCompleted -> {
                            signInWithPhoneAuthCredential(this.credentials)
                            ViewState.SuccessState(
                                VERIFY_PHONE_WITH_CODE.state,
                                "Number verified: ${phoneNumber.value}")
                        }
                        is PhoneAuthResult.Error -> {
                            ViewState.ErrorState(this.errorMessage)
                        }
                        else -> {
                            ViewState.DefaultState
                        }
                    }
                }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential?) {
        if (credential != null) {
            _viewState.value = ViewState.LoadingState
            viewModelScope.launch(Dispatchers.IO) {
                _viewState.value =
                    phoneAuthRepo.signInWithPhoneAuthCredential(credential).run {
                        when (this) {
                            is BaseResult.Success -> {
                                ViewState.SuccessState(
                                    SIGN_IN_WITH_CREDENTIAL.state, this.successMessage)
                            }
                            is BaseResult.Error -> {
                                Log.e(TAG, "signInWithPhoneAuthCredential: ${this.errorMessage}")
                                ViewState.ErrorState(this.errorMessage)
                            }
                        }
                    }
            }
        }
    }

    fun resendVerificationSmsCode(activity: Activity) {
        _viewState.value = ViewState.LoadingState
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.value =
                phoneAuthRepo.resendVerificationCode(phoneNumber.value, activity).run {
                    when (this) {
                        is PhoneAuthResult.CodeSent -> {
                            ViewState.SuccessState(
                                RESEND_CODE.state, "SMS resend to number: ${phoneNumber.value}")
                        }
                        is PhoneAuthResult.Error -> {
                            Log.e(TAG, "${this.errorMessage}")
                            ViewState.ErrorState(this.errorMessage)
                        }
                        else -> {
                            ViewState.DefaultState
                        }
                    }
                }
        }
    }

    private fun startButtonTimer() {
        object : CountDownTimer(MILLIS_IN_FUTURE, COUNT_DOWN_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                _buttonState.value =
                    PhoneAuthButtonState.TimeButtonState((millisUntilFinished / MILLIS_TO_SECONDS))
            }

            override fun onFinish() {
                _buttonState.value = PhoneAuthButtonState.TimeButtonState("Send")
            }
        }.start()
    }
}