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
import com.foxminded.android.locationtrackerkotlin.phoneauth.PhoneAuthCommonFragment
import com.foxminded.android.locationtrackerkotlin.phoneauth.PhoneAuthViewModel
import com.foxminded.android.locationtrackerkotlin.state.State
import com.foxminded.android.trackerviewer.databinding.FragmentPhoneAuthBinding
import com.foxminded.android.trackerviewer.di.config.App
import com.foxminded.android.trackerviewer.maps.MapsFragment
import javax.inject.Inject

class PhoneAuthFragment : PhoneAuthCommonFragment() {

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
        initViewsForCommonFragment()
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
        checkState()
    }

    private fun checkState() {
        lifecycleScope.launchWhenStarted {
            viewModel.phoneAuthState.collect {
                when (it) {
                    is State.SucceededState -> {
                        displayMapsFragment()
                        hideProgressIndicator(progressBar)
                    }
                    is State.ProgressIndicatorState -> {
                        showProgressIndicator(progressBar)
                    }
                    is State.ButtonTimer -> {
                        startButtonTimer(startButton)
                    }
                    is State.SmsSendState -> {
                        showToastMessage(it.value)
                        hideProgressIndicator(progressBar)
                    }
                    is State.ErrorState -> {
                        showToastMessage(it.message)
                        hideProgressIndicator(progressBar)
                    }
                    else -> {}
                }
            }
        }
    }

    private fun checkTextFields() {

        lifecycleScope.launchWhenStarted {
            textFieldPhoneListener(phoneNumberEditText, startButton).collect {
                viewModel.requestPhoneFromUser(it)
            }
        }

        lifecycleScope.launchWhenStarted {
            textFieldSmsListener(smsCodeEditText, verifyButton).collect {
                viewModel.requestCodeFromUser(it)
            }
        }
    }

    private fun initViewsForCommonFragment() {
        progressBar = binding.phoneAuthCommon.progressBarId.commonPb
        startButton = binding.phoneAuthCommon.startButton
        verifyButton = binding.phoneAuthCommon.verifyButton
        resendButton = binding.phoneAuthCommon.resendButton
        phoneNumberEditText = binding.phoneAuthCommon.phoneNumberEditText
        smsCodeEditText = binding.phoneAuthCommon.smsCodeEditText
    }

    private fun displayMapsFragment() {
        displayFragment(MapsFragment.newInstance())
    }

    companion object {
        fun newInstance() = PhoneAuthFragment()
    }
}