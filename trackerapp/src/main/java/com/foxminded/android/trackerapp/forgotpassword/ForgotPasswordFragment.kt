package com.foxminded.android.trackerapp.forgotpassword

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.foxminded.android.locationtrackerkotlin.extensions.textFieldListener
import com.foxminded.android.locationtrackerkotlin.forgotpassword.ForgotPasswordViewModel
import com.foxminded.android.locationtrackerkotlin.state.BaseViewState
import com.foxminded.android.locationtrackerkotlin.utils.StateConst.FORGOT_PASSWORD
import com.foxminded.android.locationtrackerkotlin.view.BaseCommonFragment
import com.foxminded.android.trackerapp.databinding.FragmentForgotPasswordBinding
import com.foxminded.android.trackerapp.di.config.App
import javax.inject.Inject

class ForgotPasswordFragment : BaseCommonFragment() {

    private lateinit var binding: FragmentForgotPasswordBinding
    private lateinit var emailEditText: EditText
    private lateinit var resetPasswordButton: Button
    private lateinit var progressBar: ConstraintLayout

    @Inject
    lateinit var viewModel: ForgotPasswordViewModel

    companion object {
        fun newInstance() = ForgotPasswordFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity?.application as App).mainComponent.injectForgotPasswordFragment(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        initBindingViews()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                    is BaseViewState.SuccessState -> {
                        when (it.state) {
                            FORGOT_PASSWORD.state -> {
                                showToastMessage(it.stringValue)
                                emailEditText.text.clear()
                                requireActivity().onBackPressed()
                            }
                        }
                        hideProgressIndicator(progressBar)
                    }
                    is BaseViewState.ErrorState -> {
                        showToastMessage(it.message)
                    }
                    is BaseViewState.LoadingState -> {
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

    private fun initBindingViews() {
        resetPasswordButton = binding.forgotPasswordCommon.resetPasswordButton
        emailEditText = binding.forgotPasswordCommon.emailResetPasswordEditText
        progressBar = binding.forgotPasswordCommon.progressBarId.commonPb
    }
}