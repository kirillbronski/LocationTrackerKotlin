package com.foxminded.android.trackerviewer.signup

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
import com.foxminded.android.locationtrackerkotlin.signup.SignUpViewModel
import com.foxminded.android.locationtrackerkotlin.state.BaseViewState
import com.foxminded.android.locationtrackerkotlin.state.SignUpButtonState
import com.foxminded.android.locationtrackerkotlin.view.BaseCommonFragment
import com.foxminded.android.trackerviewer.accountinfo.AccountInfoFragment
import com.foxminded.android.trackerviewer.databinding.FragmentSignUpBinding
import com.foxminded.android.trackerviewer.di.config.App
import javax.inject.Inject

private const val SIGN_UP = 1

class SignUpFragment : BaseCommonFragment() {

    private val TAG = SignUpFragment::class.java.simpleName
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var progressBar: ConstraintLayout
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var passwordAgainEditText: EditText
    private lateinit var signUpButton: Button

    @Inject
    lateinit var viewModel: SignUpViewModel

    companion object {
        fun newInstance() = SignUpFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity?.application as App).mainComponent.injectSignUpFragment(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        initBindingViews()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signUpCommon.signUpButton.setOnClickListener {
            createAccount()
        }
        checkTextFields()
        checkViewState()
        checkButtonState()
    }

    private fun createAccount() {
        viewModel.createAccount()
    }

    private fun checkViewState() {
        lifecycleScope.launchWhenStarted {
            viewModel.viewState.collect {
                when (it) {
                    is BaseViewState.SuccessState -> {
                        when (it.state) {
                            SIGN_UP -> {
                                showToastMessage(it.stringValue)
                                displayAccountInfo()
                            }
                        }
                        hideProgressIndicator(progressBar)
                    }
                    is BaseViewState.LoadingState -> {
                        showProgressIndicator(progressBar)
                    }
                    is BaseViewState.ErrorState -> {
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
                    is SignUpButtonState.IsButtonSignUpEnablerState -> {
                        when (it.enabler) {
                            true -> {
                                signUpButton.isEnabled = true
                            }
                            false -> {
                                signUpButton.isEnabled = false
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
            textFieldListener(emailEditText).collect {
                viewModel.checkAllFieldsValue(email1 = it, password1 = null, passwordAgain1 = null)
            }
        }

        lifecycleScope.launchWhenStarted {
            textFieldListener(passwordEditText).collect {
                viewModel.checkAllFieldsValue(email1 = null, password1 = it, passwordAgain1 = null)
            }
        }

        lifecycleScope.launchWhenStarted {
            textFieldListener(passwordAgainEditText).collect {
                viewModel.checkAllFieldsValue(email1 = null, password1 = null, passwordAgain1 = it)
            }
        }
    }

    private fun initBindingViews() {
        progressBar = binding.signUpCommon.progressBarId.commonPb
        emailEditText = binding.signUpCommon.emailEditText
        passwordEditText = binding.signUpCommon.passwordEditText
        passwordAgainEditText = binding.signUpCommon.passwordAgainEditText
        signUpButton = binding.signUpCommon.signUpButton
    }

    private fun displayAccountInfo() {
        displayFragment(AccountInfoFragment.newInstance(null))
    }
}