package com.foxminded.android.locationtrackerkotlin.view

import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment

abstract class BaseCommonFragment : Fragment() {

    private val TAG = BaseCommonFragment::class.java.simpleName

    protected fun showProgressIndicator(progressBar: ConstraintLayout) {
        progressBar.visibility = View.VISIBLE
    }

    protected fun hideProgressIndicator(progressBar: ConstraintLayout) {
        progressBar.visibility = View.INVISIBLE
    }

    protected fun showToastMessage(text: String?) {
        if (text != null) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }
    }

}