package com.foxminded.android.trackerapp.signup

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.foxminded.android.locationtrackerkotlin.extensions.textFieldListener
import com.foxminded.android.locationtrackerkotlin.signup.SignUpViewModel
import com.foxminded.android.locationtrackerkotlin.state.ViewState
import com.foxminded.android.locationtrackerkotlin.utils.StateEnum.DEFAULT
import com.foxminded.android.locationtrackerkotlin.utils.StateEnum.SIGN_UP
import com.foxminded.android.locationtrackerkotlin.view.BaseCommonFragment
import com.foxminded.android.trackerapp.R
import com.foxminded.android.trackerapp.databinding.FragmentSignUpBinding
import com.foxminded.android.trackerapp.di.config.App
import javax.inject.Inject

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
                    is ViewState.SuccessState -> {
                        when (it.state) {
                            SIGN_UP.state -> {
                                showToastMessage(it.stringValue)
                                displayAccountInfo()
                                it.state = DEFAULT.state
                                it.stringValue = null
                            }
                        }
                        hideProgressIndicator(progressBar)
                        emailEditText.text.clear()
                        passwordEditText.text.clear()
                        passwordAgainEditText.text.clear()
                    }
                    is ViewState.LoadingState -> {
                        showProgressIndicator(progressBar)
                    }
                    is ViewState.ErrorState -> {
                        showToastMessage(it.message)
                        hideProgressIndicator(progressBar)
                        it.message = null
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
                        signUpButton.isEnabled = true
                    }
                    false -> {
                        signUpButton.isEnabled = false
                    }
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
        findNavController().navigate(R.id.action_signUpFragment_to_accountInfoFragment)
    }
}