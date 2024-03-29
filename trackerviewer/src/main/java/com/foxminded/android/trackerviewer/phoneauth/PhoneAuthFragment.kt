package com.foxminded.android.trackerviewer.phoneauth

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
import com.foxminded.android.locationtrackerkotlin.phoneauth.PhoneAuthViewModel
import com.foxminded.android.locationtrackerkotlin.state.PhoneAuthButtonState
import com.foxminded.android.locationtrackerkotlin.state.ViewState
import com.foxminded.android.locationtrackerkotlin.utils.StateEnum.*
import com.foxminded.android.locationtrackerkotlin.view.BaseFragment
import com.foxminded.android.trackerviewer.databinding.FragmentPhoneAuthBinding
import com.foxminded.android.trackerviewer.di.config.App
import javax.inject.Inject

class PhoneAuthFragment : BaseFragment<FragmentPhoneAuthBinding>() {

    private val TAG = PhoneAuthFragment::class.java.simpleName
    private lateinit var progressBar: ConstraintLayout
    private lateinit var startButton: Button
    private lateinit var verifyButton: Button
    private lateinit var resendButton: Button
    private lateinit var phoneNumberEditText: EditText
    private lateinit var smsCodeEditText: EditText

    @Inject
    lateinit var viewModel: PhoneAuthViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity?.application as App).mainComponent.injectPhoneAuthFragment(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBindingViews()

        replaceImageAndIconColor()
        startButton.setOnClickListener {
            viewModel.verifyPhoneNumber(requireActivity())
        }

        verifyButton.setOnClickListener {
            viewModel.verifyPhoneNumberWithCode()
            resendButton.isEnabled = true
        }

        resendButton.setOnClickListener {
            viewModel.resendVerificationSmsCode(requireActivity())
            resendButton.isEnabled = false
        }
        checkTextFields()
        checkViewState()
        checkButtonState()
    }

    private fun checkViewState() {
        lifecycleScope.launchWhenStarted {
            viewModel.viewState.collect {
                when (it) {
                    is ViewState.SuccessState -> {
                        when (it.state) {
                            SEND_SMS.state -> {
                                showToastMessage(it.stringValue)
                                it.stringValue = null
                            }
                            VERIFY_PHONE_WITH_CODE.state -> {
                                showToastMessage(it.stringValue)
                                it.stringValue = null
                            }
                            SIGN_IN_WITH_CREDENTIAL.state -> {
                                displayAccountInfoFragment(it.stringValue)
                                it.state = DEFAULT.state
                                it.stringValue = null
                            }
                            RESEND_CODE.state -> {
                                showToastMessage(it.stringValue)
                                it.stringValue = null
                            }

                        }
                        hideProgressIndicator(progressBar)
                    }
                    is ViewState.LoadingState -> {
                        showProgressIndicator(progressBar)
                    }
                    is ViewState.ErrorState -> {
                        showToastMessage(it.message)
                        hideProgressIndicator(progressBar)
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
                    is PhoneAuthButtonState.TimeButtonState<*> -> {
                        startButton.isEnabled = false
                        when (it.text) {
                            is Long -> {
                                startButton.text = it.text.toString()
                                phoneNumberEditText.isEnabled = false
                            }
                            is String -> {
                                startButton.text = it.text.toString()
                                startButton.isEnabled = true
                                phoneNumberEditText.isEnabled = true
                            }
                        }
                    }
                    is PhoneAuthButtonState.IsButtonSendEnablerState -> {
                        when (it.enabler) {
                            true -> {
                                startButton.isEnabled = true
                            }
                            false -> {
                                startButton.isEnabled = false
                            }
                        }
                    }
                    is PhoneAuthButtonState.IsButtonVerifyEnablerState -> {
                        when (it.enabler) {
                            true -> {
                                verifyButton.isEnabled = true
                            }
                            false -> {
                                verifyButton.isEnabled = false
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun checkTextFields() {

        lifecycleScope.launchWhenStarted {
            textFieldListener(phoneNumberEditText).collect {
                viewModel.checkPhoneField(it)
            }
        }

        lifecycleScope.launchWhenStarted {
            textFieldListener(smsCodeEditText).collect {
                viewModel.checkSmsCodeField(it)
            }
        }
    }

    private fun replaceImageAndIconColor() {
        val drawable = ContextCompat.getDrawable(requireContext(),
            com.foxminded.android.locationtrackerkotlin.R.drawable.ic_phone_viewer)
        binding.phoneAuthCommon.phoneNumberEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(
            com.foxminded.android.locationtrackerkotlin.R.drawable.ic_baseline_local_phone_v,
            0,
            0,
            0)
        binding.phoneAuthCommon.smsCodeEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(
            com.foxminded.android.locationtrackerkotlin.R.drawable.ic_baseline_sms_24, 0, 0, 0)
        binding.phoneAuthCommon.imageView.setImageDrawable(drawable)
    }

    private fun initBindingViews() {
        progressBar = binding.phoneAuthCommon.progressBarId.commonPb
        startButton = binding.phoneAuthCommon.startButton
        verifyButton = binding.phoneAuthCommon.verifyButton
        resendButton = binding.phoneAuthCommon.resendButton
        phoneNumberEditText = binding.phoneAuthCommon.phoneNumberEditText
        smsCodeEditText = binding.phoneAuthCommon.smsCodeEditText
    }

    private fun displayAccountInfoFragment(accountInfo: String?) {
        findNavController().navigate(PhoneAuthFragmentDirections
            .actionPhoneAuthFragmentToAccountInfoFragment(accountInfo))
    }

    override fun getViewBinding() = FragmentPhoneAuthBinding.inflate(layoutInflater)
}