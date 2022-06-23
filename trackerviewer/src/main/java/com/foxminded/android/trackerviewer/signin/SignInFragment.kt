package com.foxminded.android.trackerviewer.signin

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.foxminded.android.locationtrackerkotlin.extensions.textFieldListener
import com.foxminded.android.locationtrackerkotlin.signin.SignInViewModel
import com.foxminded.android.locationtrackerkotlin.state.ViewState
import com.foxminded.android.locationtrackerkotlin.utils.StateEnum.*
import com.foxminded.android.locationtrackerkotlin.view.BaseCommonFragment
import com.foxminded.android.trackerviewer.R
import com.foxminded.android.trackerviewer.databinding.FragmentSignInBinding
import com.foxminded.android.trackerviewer.di.config.App
import javax.inject.Inject

class SignInFragment : BaseCommonFragment() {

    private val TAG = SignInFragment::class.java.simpleName
    private lateinit var binding: FragmentSignInBinding
    private lateinit var progressBar: ConstraintLayout
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signInButton: Button

    @Inject
    lateinit var viewModel: SignInViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity?.application as App).mainComponent.injectSignInFragment(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.requestAccountInfo()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        initBindingViews()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        replaceImageAndIconColor()

        binding.loginFragmentCommon.signInButton.setOnClickListener { viewModel.signIn() }
        binding.loginFragmentCommon.phoneButton.setOnClickListener { displayPhoneAuthFragment() }
        binding.loginFragmentCommon.signUpTextView.setOnClickListener { displaySignUpFragment() }
        binding.loginFragmentCommon.forgotPasswordTextView.setOnClickListener {
            displayForgotPasswordFragment()
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
                            SIGN_IN.state -> {
                                showToastMessage(it.stringValue)
                                displayAccountInfoFragment()
                                it.state = DEFAULT.state
                                it.stringValue = null
                            }
                            ACCOUNT.state -> {
                                displayAccountInfoFragment()
                                it.state = DEFAULT.state
                            }
                        }
                        hideProgressIndicator(progressBar)
                        emailEditText.text.clear()
                        passwordEditText.text.clear()
                    }
                    is ViewState.LoadingState -> {
                        showProgressIndicator(progressBar)
                    }
                    is ViewState.ErrorState -> {
                        showToastMessage(it.message)
                        hideProgressIndicator(progressBar)
                        it.message = null
                    }
                    else -> {
                        Log.d(TAG, "checkViewState: BRANCH ELSE")
                    }
                }
            }
        }
    }

    private fun checkButtonState() {
        lifecycleScope.launchWhenStarted {
            viewModel.signInButtonState.collect {
                when (it) {
                    true -> {
                        signInButton.isEnabled = true
                    }
                    false -> {
                        signInButton.isEnabled = false
                    }
                }
            }
        }
    }

    private fun checkTextFields() {

        lifecycleScope.launchWhenStarted {
            textFieldListener(emailEditText).collect {
                viewModel.checkEmailAndPasswordFieldsValue(email1 = it, password1 = null)
            }
        }

        lifecycleScope.launchWhenStarted {
            textFieldListener(passwordEditText).collect {
                viewModel.checkEmailAndPasswordFieldsValue(email1 = null, password1 = it)
            }
        }
    }

    private fun replaceImageAndIconColor() {
        val drawable = ContextCompat.getDrawable(requireContext(),
            com.foxminded.android.locationtrackerkotlin.R.drawable.ic_main_viewer)
        binding.loginFragmentCommon.emailEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(
            com.foxminded.android.locationtrackerkotlin.R.drawable.ic_email_viewerr, 0, 0, 0)
        binding.loginFragmentCommon.passwordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(
            com.foxminded.android.locationtrackerkotlin.R.drawable.ic__lock_viewer, 0, 0, 0)
        binding.loginFragmentCommon.imageView.setImageDrawable(drawable)
    }

    private fun initBindingViews() {
        progressBar = binding.loginFragmentCommon.progressBarId.commonPb
        emailEditText = binding.loginFragmentCommon.emailEditText
        passwordEditText = binding.loginFragmentCommon.passwordEditText
        signInButton = binding.loginFragmentCommon.signInButton
    }

    private fun displayPhoneAuthFragment() {
        findNavController().navigate(R.id.action_signInFragment_to_phoneAuthFragment)
    }

    private fun displayForgotPasswordFragment() {
        findNavController().navigate(R.id.action_signInFragment_to_forgotPasswordFragment)
    }

    private fun displayAccountInfoFragment() {
        findNavController().navigate(R.id.action_signInFragment_to_accountInfoFragment)
    }

    private fun displaySignUpFragment() {
        findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
    }
}