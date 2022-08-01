package com.foxminded.android.trackerviewer.forgotpassword

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.foxminded.android.locationtrackerkotlin.extensions.textFieldListener
import com.foxminded.android.locationtrackerkotlin.forgotpassword.ForgotPasswordViewModel
import com.foxminded.android.locationtrackerkotlin.state.ViewState
import com.foxminded.android.locationtrackerkotlin.utils.StateEnum.DEFAULT
import com.foxminded.android.locationtrackerkotlin.utils.StateEnum.FORGOT_PASSWORD
import com.foxminded.android.locationtrackerkotlin.view.BaseFragment
import com.foxminded.android.trackerviewer.databinding.FragmentForgotPasswordBinding
import com.foxminded.android.trackerviewer.di.config.App
import javax.inject.Inject

class ForgotPasswordFragment : BaseFragment<FragmentForgotPasswordBinding>() {

    private lateinit var emailEditText: EditText
    private lateinit var resetPasswordButton: Button
    private lateinit var progressBar: ConstraintLayout

    @Inject
    lateinit var viewModel: ForgotPasswordViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity?.application as App).mainComponent.injectForgotPasswordFragment(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBindingViews()
        replaceImageAndIconColor()
        resetPasswordButton.setOnClickListener {
            viewModel.passwordReset()
        }

        checkViewState()
        checkButtonState()
        checkTextFields()
    }

    private fun checkViewState() {
        lifecycleScope.launchWhenStarted {
            viewModel.viewState.collect {
                when (it) {
                    is ViewState.SuccessState -> {
                        when (it.state) {
                            FORGOT_PASSWORD.state -> {
                                showToastMessage(it.stringValue)
                                findNavController().popBackStack()
                                emailEditText.text.clear()
                                it.stringValue = null
                                it.state = DEFAULT.state
                            }
                        }
                        hideProgressIndicator(progressBar)
                    }
                    is ViewState.ErrorState -> {
                        showToastMessage(it.message)
                        it.message = null
                    }
                    is ViewState.LoadingState -> {
                        showProgressIndicator(progressBar)
                    }
                    else -> {}
                }
            }
        }
    }

    private fun checkButtonState() {
        lifecycleScope.launchWhenStarted {
            viewModel.buttonState.collect {
                when (it) {
                    true -> {
                        resetPasswordButton.isEnabled = true
                    }
                    false -> {
                        resetPasswordButton.isEnabled = false
                    }
                }
            }
        }
    }

    private fun checkTextFields() {
        lifecycleScope.launchWhenStarted {
            textFieldListener(emailEditText).collect {
                viewModel.checkEmailField(emailFieldValue = it)
            }
        }
    }

    private fun replaceImageAndIconColor() {
        val drawable = ContextCompat.getDrawable(requireContext(),
            com.foxminded.android.locationtrackerkotlin.R.drawable.ic_lock_open_viewer)
        binding.forgotPasswordCommon.emailResetPasswordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(
            com.foxminded.android.locationtrackerkotlin.R.drawable.ic_email_viewerr, 0, 0, 0)
        binding.forgotPasswordCommon.imageView.setImageDrawable(drawable)
    }

    private fun initBindingViews() {
        resetPasswordButton = binding.forgotPasswordCommon.resetPasswordButton
        emailEditText = binding.forgotPasswordCommon.emailResetPasswordEditText
        progressBar = binding.forgotPasswordCommon.progressBarId.commonPb
    }

    override fun getViewBinding() = FragmentForgotPasswordBinding.inflate(layoutInflater)
}