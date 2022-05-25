package com.foxminded.android.locationtrackerkotlin.phoneauth

import android.app.Activity
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foxminded.android.locationtrackerkotlin.state.BaseViewState
import com.foxminded.android.locationtrackerkotlin.state.PhoneAuthButtonState
import com.google.firebase.auth.PhoneAuthCredential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val MILLIS_IN_FUTURE = 60000L
private const val COUNT_DOWN_INTERVAL = 1000L
private const val MILLIS_TO_SECONDS = 1000L
private const val SEND_SMS = 1
private const val VERIFY_PHONE_WITH_CODE = 2
private const val SIGN_IN_WITH_CREDENTIAL = 3
private const val RESEND_CODE = 4

class PhoneAuthViewModel(
    private var phoneAuthRepo: IPhoneAuthRepo,
) : ViewModel() {

    private val TAG = PhoneAuthViewModel::class.java.simpleName

    private var phoneNumber = MutableStateFlow("")
    private var smsCode = MutableStateFlow("")

    private val _viewState = MutableStateFlow<BaseViewState>(BaseViewState.DefaultState)
    val viewState: StateFlow<BaseViewState> = _viewState.asStateFlow()
    private val _buttonState =
        MutableStateFlow<PhoneAuthButtonState>(PhoneAuthButtonState.DefaultState)
    val buttonState: StateFlow<PhoneAuthButtonState> = _buttonState.asStateFlow()

    fun checkPhoneField(phoneNumber: String): String {
        if (phoneNumber.isNotEmpty()
            && phoneNumber.length >= 12 && phoneNumber.contains("+")
        ) {
            _buttonState.value = PhoneAuthButtonState.IsButtonSendEnablerState(true)
        } else {
            _buttonState.value = PhoneAuthButtonState.IsButtonSendEnablerState(false)
        }
        return phoneNumber.also { this.phoneNumber.value = it }
    }

    fun checkSmsCodeField(smsCode: String): String {
        if (smsCode.isNotEmpty() && smsCode.length >= 6) {
            _buttonState.value = PhoneAuthButtonState.IsButtonVerifyEnablerState(true)
        } else {
            _buttonState.value = PhoneAuthButtonState.IsButtonVerifyEnablerState(false)
        }
        return smsCode.also { this.smsCode.value = it }
    }

    fun verifyPhoneNumber(activity: Activity) {
        _viewState.value = BaseViewState.LoadingState
        startButtonTimer()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = phoneAuthRepo.verifyPhoneNumber(phoneNumber.value, activity)
                Log.d(TAG, "verifyPhoneNumberViewModel: $result")
                _viewState.value =
                    BaseViewState.SuccessState(SEND_SMS, "SMS send to number: ${phoneNumber.value}")
            } catch (e: Exception) {
                Log.e(TAG, "${e.message}", e)
                _viewState.value = BaseViewState.ErrorState(e.message.toString())
            }
        }
    }

    fun verifyPhoneNumberWithCode() {
        _viewState.value = BaseViewState.LoadingState
        viewModelScope.launch(Dispatchers.IO) {
            try {
                phoneAuthRepo.verifyPhoneNumberWithCode(smsCode.value).run {
                    _viewState.value = BaseViewState.SuccessState(
                        VERIFY_PHONE_WITH_CODE, "Number verified: ${phoneNumber.value}")
                    signInWithPhoneAuthCredential(this)
                }
            } catch (e: Exception) {
                _viewState.value = BaseViewState.ErrorState(e.message.toString())
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential?) {
        if (credential != null) {
            _viewState.value = BaseViewState.LoadingState
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    phoneAuthRepo.signInWithPhoneAuthCredential(credential)
                    _viewState.value = BaseViewState.SuccessState(
                        SIGN_IN_WITH_CREDENTIAL, phoneNumber.value)
                } catch (e: Exception) {
                    Log.e(TAG, "signInWithPhoneAuthCredential: ${e.message}", e)
                    _viewState.value = BaseViewState.ErrorState(e.message.toString())
                }
            }
        }
    }

    fun resendVerificationSmsCode(activity: Activity) {
        _viewState.value = BaseViewState.LoadingState
        viewModelScope.launch(Dispatchers.IO) {
            try {
                phoneAuthRepo.resendVerificationCode(phoneNumber.value, activity)
                _viewState.value = BaseViewState.SuccessState(
                    RESEND_CODE, "SMS resend to number: ${phoneNumber.value}")
            } catch (e: Exception) {
                Log.e(TAG, "${e.message}", e)
                _viewState.value = BaseViewState.ErrorState(e.message.toString())
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