package com.foxminded.android.locationtrackerkotlin.signup

import android.widget.Button
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import com.foxminded.android.locationtrackerkotlin.view.BaseCommonFragment
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

abstract class SignUpCommonFragment: BaseCommonFragment() {

    private var resultPassword = ""

    fun textFieldPasswordListener(password: EditText): Flow<String> {
        return callbackFlow {
            password.addTextChangedListener {
                if (it.toString().length >= 6) {
                    resultPassword = it.toString()
                    trySend(it.toString())
                }
            }
            awaitClose()
        }
    }

    fun textFieldPasswordAgainListener(passwordAgain: EditText, button: Button): Flow<String> {
       return callbackFlow {
           passwordAgain.addTextChangedListener {
               if (it.toString().length >= 6 && it.toString() == resultPassword) {
                   trySend(it.toString())
                   button.isEnabled = true
               } else {
                   button.isEnabled = false
               }
           }
           awaitClose()
       }
    }

}