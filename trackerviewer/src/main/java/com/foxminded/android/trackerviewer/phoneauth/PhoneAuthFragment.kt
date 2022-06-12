package com.foxminded.android.trackerviewer.phoneauth

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
import com.foxminded.android.locationtrackerkotlin.phoneauth.PhoneAuthViewModel
import com.foxminded.android.locationtrackerkotlin.state.ViewState
import com.foxminded.android.locationtrackerkotlin.state.PhoneAuthButtonState
import com.foxminded.android.locationtrackerkotlin.utils.StateConst.*
import com.foxminded.android.locationtrackerkotlin.view.BaseCommonFragment
import com.foxminded.android.trackerviewer.accountinfo.AccountInfoFragment
import com.foxminded.android.trackerviewer.databinding.FragmentPhoneAuthBinding
import com.foxminded.android.trackerviewer.di.config.App
import javax.inject.Inject

class PhoneAuthFragment : BaseCommonFragment() {

    private val TAG = PhoneAuthFragment::class.java.simpleName
    private lateinit var binding: FragmentPhoneAuthBinding
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPhoneAuthBinding.inflate(inflater, container, false)
        initBindingViews()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                            }
                            VERIFY_PHONE_WITH_CODE.state -> {
                                showToastMessage(it.stringValue)
                            }
                            SIGN_IN_WITH_CREDENTIAL.state -> {
                                displayAccountInfoFragment(it.stringValue)
                            }
                            RESEND_CODE.state -> {
                                showToastMessage(it.stringValue)
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

    private fun initBindingViews() {
        progressBar = binding.phoneAuthCommon.progressBarId.commonPb
        startButton = binding.phoneAuthCommon.startButton
        verifyButton = binding.phoneAuthCommon.verifyButton
        resendButton = binding.phoneAuthCommon.resendButton
        phoneNumberEditText = binding.phoneAuthCommon.phoneNumberEditText
        smsCodeEditText = binding.phoneAuthCommon.smsCodeEditText
    }

    private fun displayAccountInfoFragment(accountInfo: String?) {
        displayFragment(AccountInfoFragment.newInstance(accountInfo))
    }

    companion object {
        fun newInstance() = PhoneAuthFragment()
    }
}