package com.foxminded.android.locationtrackerkotlin.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<viewBinding : ViewBinding> : Fragment() {

    private val TAG = BaseFragment::class.java.simpleName

    private var _binding: viewBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = getViewBinding()
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    abstract fun getViewBinding(): viewBinding

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