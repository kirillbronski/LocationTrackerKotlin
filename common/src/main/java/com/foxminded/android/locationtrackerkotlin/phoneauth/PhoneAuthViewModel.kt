package com.foxminded.android.locationtrackerkotlin.phoneauth

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foxminded.android.locationtrackerkotlin.extensions.set
import com.foxminded.android.locationtrackerkotlin.state.State
import com.google.firebase.auth.PhoneAuthCredential
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PhoneAuthViewModel(
    private var phoneAuthRepo: IPhoneAuthRepo,
) : ViewModel() {

    private val TAG = PhoneAuthViewModel::class.java.simpleName

    private var phoneNumber: String = ""
    private var smsCode: String = ""

    fun requestPhoneFromUser(phoneNumber: String): String {
        return phoneNumber.also { this.phoneNumber = it }
    }

    fun requestCodeFromUser(smsCode: String): String {
        return smsCode.also { this.smsCode = it }
    }

    private val _phoneAuthState = MutableStateFlow<State>(State.DefaultState)
    val phoneAuthState: StateFlow<State> = _phoneAuthState.asStateFlow()

    fun verifyPhoneNumber(activity: Activity) {
        if (validatePhoneNumber()) {
            _phoneAuthState.set(State.ProgressIndicatorState)
            _phoneAuthState.set(State.ButtonTimer)
            viewModelScope.async {
                try {
                    val result = phoneAuthRepo.verifyPhoneNumber(phoneNumber, activity)
                    Log.d(TAG, "verifyPhoneNumberViewModel: $result")
                    _phoneAuthState.set(State.SmsSendState("SMS send to number: $phoneNumber"))
                } catch (e: Exception) {
                    Log.e(TAG, "${e.message}", e)
                    _phoneAuthState.set(State.ErrorState(e.message.toString()))
                }
            }
        }
    }

    fun verifyPhoneNumberWithCode() {
        if (validateSmsCode()) {
            _phoneAuthState.set(State.ProgressIndicatorState)
            viewModelScope.async {
                try {
                    phoneAuthRepo.verifyPhoneNumberWithCode(smsCode).run {
                        _phoneAuthState.set(State.SmsSendState("Success!"))
                        signInWithPhoneAuthCredential(this)
                    }
                } catch (e: Exception) {
                    _phoneAuthState.set(State.ErrorState(e.message.toString()))
                }
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential?) {
        if (credential != null) {
            _phoneAuthState.set(State.ProgressIndicatorState)
            viewModelScope.launch {
                try {
                    phoneAuthRepo.signInWithPhoneAuthCredential(credential)
                    _phoneAuthState.set(State.SucceededState)
                } catch (e: Exception) {
                    Log.e(TAG, "signInWithPhoneAuthCredential: ${e.message}", e)
                    _phoneAuthState.set(State.ErrorState(e.message.toString()))
                }
            }
        }
    }

    fun resendVerificationSmsCode(activity: Activity) {
        if (validatePhoneNumber()) {
            _phoneAuthState.set(State.ProgressIndicatorState)
            viewModelScope.async {
                try {
                    phoneAuthRepo.resendVerificationCode(phoneNumber, activity)
                    _phoneAuthState.set(State.SmsSendState("SMS resend to number: $phoneNumber"))
                } catch (e: Exception) {
                    Log.e(TAG, "${e.message}", e)
                    _phoneAuthState.set(State.ErrorState(e.message.toString()))
                }
            }
        }
    }

    private fun validatePhoneNumber(): Boolean {
        return if (phoneNumber.isNotEmpty()
            && phoneNumber.length >= 12 && phoneNumber.contains("+")
        ) {
            true
        } else {
            _phoneAuthState.set(State.ErrorState("Invalid phone number!"))
            false
        }
    }

    private fun validateSmsCode(): Boolean {
        return if (smsCode.isNotEmpty() || smsCode.length >= 6) {
            true
        } else {
            _phoneAuthState.set(State.ErrorState("Invalid sms code!"))
            false
        }
    }
}