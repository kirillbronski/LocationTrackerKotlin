package com.foxminded.android.locationtrackerkotlin.signin

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.Group
import androidx.core.widget.addTextChangedListener
import com.foxminded.android.locationtrackerkotlin.R
import com.foxminded.android.locationtrackerkotlin.view.BaseCommonFragment
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

abstract class SignInCommonFragment : BaseCommonFragment() {

    fun forgotPassword(forgotPasswordGroup: Group, signInGroup: Group) {
        forgotPasswordGroup.visibility = View.VISIBLE
        signInGroup.visibility = View.GONE
    }

    fun backButton(
        forgotPasswordGroup: Group,
        signInGroup: Group,
        emailResetPasswordEditText: EditText,
    ) {
        forgotPasswordGroup.visibility = View.GONE
        signInGroup.visibility = View.VISIBLE
        emailResetPasswordEditText.text?.clear()
    }

    fun returnSignInView(forgotPasswordGroup: Group, signInGroup: Group) {
        forgotPasswordGroup.visibility = View.GONE
        signInGroup.visibility = View.VISIBLE
    }

    fun signOutButton(signInGroup: Group, showAccountInfoGroup: Group) {
        signInGroup.visibility = View.VISIBLE
        showAccountInfoGroup.visibility = View.GONE
    }

    fun continueOrSignOut(
        accountInfo: String,
        accountTextView: TextView,
        showAccountInfoGroup: Group,
        signInGroup: Group,
    ) {
        accountTextView.text = String.format("%s %s",
            getString(R.string.your_account_text_view), accountInfo)
        showAccountInfoGroup.visibility = View.VISIBLE
        signInGroup.visibility = View.GONE
    }

    fun showResetPasswordMessage(email: String?) {
        Toast.makeText(context,
            "Password reset link sent to: $email",
            Toast.LENGTH_SHORT).show()
    }

    fun textFieldPasswordListener(password: EditText, button: Button): Flow<String> {
        return callbackFlow {
            password.addTextChangedListener {
                if (it.toString().length >= 6) {
                    trySend(it.toString())
                    button.isEnabled = true
                } else {
                    button.isEnabled = false
                }
            }
            awaitClose()
        }
    }

    fun textFieldResetPasswordListener(email: EditText, button: Button): Flow<String> {
        return callbackFlow {
            email.addTextChangedListener {
                if (it.toString().isNotEmpty() && it.toString().contains("@")
                    && it.toString().contains(".")
                ) {
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