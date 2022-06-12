package com.foxminded.android.trackerapp.signin

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.foxminded.android.locationtrackerkotlin.extensions.textFieldListener
import com.foxminded.android.locationtrackerkotlin.signin.SignInViewModel
import com.foxminded.android.locationtrackerkotlin.state.ViewState
import com.foxminded.android.locationtrackerkotlin.utils.StateConst.*
import com.foxminded.android.locationtrackerkotlin.view.BaseCommonFragment
import com.foxminded.android.trackerapp.accountinfo.AccountInfoFragment
import com.foxminded.android.trackerapp.databinding.FragmentSignInBinding
import com.foxminded.android.trackerapp.di.config.App
import com.foxminded.android.trackerapp.forgotpassword.ForgotPasswordFragment
import com.foxminded.android.trackerapp.phoneauth.PhoneAuthFragment
import com.foxminded.android.trackerapp.signup.SignUpFragment
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

    companion object {
        fun newInstance() = SignInFragment()
    }

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
                            }
                            ACCOUNT.state -> {
                                displayAccountInfoFragment()
                                it.state = DEFAULT.state
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

    private fun initBindingViews() {
        progressBar = binding.loginFragmentCommon.progressBarId.commonPb
        emailEditText = binding.loginFragmentCommon.emailEditText
        passwordEditText = binding.loginFragmentCommon.passwordEditText
        signInButton = binding.loginFragmentCommon.signInButton
    }

    private fun displayPhoneAuthFragment() {
        displayFragment(PhoneAuthFragment.newInstance())
    }

    private fun displayForgotPasswordFragment() {
        displayFragment(ForgotPasswordFragment.newInstance())
    }

    private fun displayAccountInfoFragment() {
        displayFragment(AccountInfoFragment.newInstance(null))
    }

    private fun displaySignUpFragment() {
        displayFragment(SignUpFragment.newInstance())
    }

}