package com.foxminded.android.locationtrackerkotlin.view

import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.foxminded.android.locationtrackerkotlin.MainActivityCommon
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

abstract class BaseCommonFragment : Fragment() {

    private val TAG = BaseCommonFragment::class.java.simpleName
    private var resultPassword = ""

    fun displayFragment(fragment: Fragment) {
        (requireActivity() as MainActivityCommon).displayFragment(fragment)
    }

    fun showProgressIndicator(progressBar: ConstraintLayout) {
        progressBar.visibility = View.VISIBLE
    }

    fun hideProgressIndicator(progressBar: ConstraintLayout) {
        progressBar.visibility = View.INVISIBLE
    }

    fun showToastMessage(text: String?) {
        Log.d(TAG, "onViewCreated: $text")
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    fun textFieldEmailListener(email: EditText): Flow<String> {
        return callbackFlow {
            email.addTextChangedListener {
                if (it.toString().isNotEmpty() && it.toString().contains("@")
                    && it.toString().contains(".")
                ) {
                    trySend(it.toString())
                }
            }
            awaitClose()
        }
    }
}