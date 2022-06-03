package com.foxminded.android.locationtrackerkotlin.phoneauth

import android.app.Activity
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foxminded.android.locationtrackerkotlin.extensions.isValidPhone
import com.foxminded.android.locationtrackerkotlin.extensions.isValidSmsCode
import com.foxminded.android.locationtrackerkotlin.state.BaseViewState
import com.foxminded.android.locationtrackerkotlin.state.PhoneAuthButtonState
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

    private val _viewState = MutableStateFlow<BaseViewState>(BaseViewState.DefaultState)
    val viewState: StateFlow<BaseViewState> = _viewState.asStateFlow()
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
        _viewState.value = BaseViewState.LoadingState
        startButtonTimer()
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.value = try {
                val result = phoneAuthRepo.verifyPhoneNumber(phoneNumber.value, activity)
                Log.d(TAG, "verifyPhoneNumberViewModel: $result")
                BaseViewState.SuccessState(SEND_SMS.state,
                    "SMS send to number: ${phoneNumber.value}")
            } catch (e: Exception) {
                Log.e(TAG, "${e.message}", e)
                BaseViewState.ErrorState(e.message.toString())
            }
        }
    }

    fun verifyPhoneNumberWithCode() {
        _viewState.value = BaseViewState.LoadingState
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.value = try {
                phoneAuthRepo.verifyPhoneNumberWithCode(smsCode.value).run {
                    signInWithPhoneAuthCredential(this)
                    BaseViewState.SuccessState(
                        VERIFY_PHONE_WITH_CODE.state, "Number verified: ${phoneNumber.value}")
                }
            } catch (e: Exception) {
                BaseViewState.ErrorState(e.message.toString())
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential?) {
        if (credential != null) {
            _viewState.value = BaseViewState.LoadingState
            viewModelScope.launch(Dispatchers.IO) {
                _viewState.value = try {
                    phoneAuthRepo.signInWithPhoneAuthCredential(credential)
                    BaseViewState.SuccessState(
                        SIGN_IN_WITH_CREDENTIAL.state, phoneNumber.value)
                } catch (e: Exception) {
                    Log.e(TAG, "signInWithPhoneAuthCredential: ${e.message}", e)
                    BaseViewState.ErrorState(e.message.toString())
                }
            }
        }
    }

    fun resendVerificationSmsCode(activity: Activity) {
        _viewState.value = BaseViewState.LoadingState
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.value = try {
                phoneAuthRepo.resendVerificationCode(phoneNumber.value, activity)
                BaseViewState.SuccessState(
                    RESEND_CODE.state, "SMS resend to number: ${phoneNumber.value}")
            } catch (e: Exception) {
                Log.e(TAG, "${e.message}", e)
                BaseViewState.ErrorState(e.message.toString())
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