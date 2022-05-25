package com.foxminded.android.locationtrackerkotlin.view

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.foxminded.android.locationtrackerkotlin.CommonActivity

abstract class BaseCommonFragment : Fragment() {

    private val TAG = BaseCommonFragment::class.java.simpleName

    protected fun displayFragment(fragment: Fragment) {
        (requireActivity() as CommonActivity).displayFragment(fragment)
    }

    protected fun showProgressIndicator(progressBar: ConstraintLayout) {
        progressBar.visibility = View.VISIBLE
    }

    protected fun hideProgressIndicator(progressBar: ConstraintLayout) {
        progressBar.visibility = View.INVISIBLE
    }

    protected fun showToastMessage(text: String?) {
        Log.d(TAG, "onViewCreated: $text")
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

}