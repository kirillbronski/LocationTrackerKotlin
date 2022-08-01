package com.foxminded.android.trackerviewer.signup

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
import com.foxminded.android.locationtrackerkotlin.signup.SignUpViewModel
import com.foxminded.android.locationtrackerkotlin.state.ViewState
import com.foxminded.android.locationtrackerkotlin.utils.StateEnum.SIGN_UP
import com.foxminded.android.locationtrackerkotlin.view.BaseFragment
import com.foxminded.android.trackerviewer.R
import com.foxminded.android.trackerviewer.databinding.FragmentSignUpBinding
import com.foxminded.android.trackerviewer.di.config.App
import javax.inject.Inject

class SignUpFragment : BaseFragment<FragmentSignUpBinding>() {

    private val TAG = SignUpFragment::class.java.simpleName
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBindingViews()
        replaceImageAndIconColor()
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
                                it.state = null
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
                viewModel.checkAllFieldsValue(email1 = it)
            }
        }

        lifecycleScope.launchWhenStarted {
            textFieldListener(passwordEditText).collect {
                viewModel.checkAllFieldsValue(password1 = it)
            }
        }

        lifecycleScope.launchWhenStarted {
            textFieldListener(passwordAgainEditText).collect {
                viewModel.checkAllFieldsValue(passwordAgain1 = it)
            }
        }
    }

    private fun replaceImageAndIconColor() {
        val drawable = ContextCompat.getDrawable(requireContext(),
            com.foxminded.android.locationtrackerkotlin.R.drawable.ic_person_viewer)
        binding.signUpCommon.emailEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(
            com.foxminded.android.locationtrackerkotlin.R.drawable.ic_email_viewerr, 0, 0, 0)
        binding.signUpCommon.passwordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(
            com.foxminded.android.locationtrackerkotlin.R.drawable.ic__lock_viewer, 0, 0, 0)
        binding.signUpCommon.passwordAgainEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(
            com.foxminded.android.locationtrackerkotlin.R.drawable.ic__lock_viewer, 0, 0, 0)
        binding.signUpCommon.imageView.setImageDrawable(drawable)
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

    override fun getViewBinding() = FragmentSignUpBinding.inflate(layoutInflater)
}