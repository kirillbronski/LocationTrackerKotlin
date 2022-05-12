package com.foxminded.android.locationtrackerkotlin.phoneauth

import android.os.CountDownTimer
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import com.foxminded.android.locationtrackerkotlin.view.BaseCommonFragment
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

abstract class PhoneAuthCommonFragment : BaseCommonFragment() {

    fun textFieldPhoneListener(phoneNumber: EditText, button: Button): Flow<String> {
        return callbackFlow {
            phoneNumber.addTextChangedListener {
                if (it.toString().isNotEmpty() && it.toString().length >= 12) {
                    trySend(it.toString())
                    button.isEnabled = true
                } else {
                    button.isEnabled = false
                }
            }
            awaitClose()
        }
    }

    fun textFieldSmsListener(smsCode: EditText, button: Button): Flow<String> {
        return callbackFlow {
            smsCode.addTextChangedListener {
                if (it.toString().isNotEmpty() && it.toString().length >= 6) {
                    trySend(it.toString())
                    button.isEnabled = true
                } else {
                    button.isEnabled = false
                }
            }
            awaitClose()
        }
    }

    fun startButtonTimer(button: Button) {
        object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                button.isEnabled = false
                button.text = (millisUntilFinished / 1000).toString()
            }
            override fun onFinish() {
                button.isEnabled = true
                button.text = "Send"
            }
        }.start()
    }

}