package com.foxminded.android.trackerviewer.signin

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.lifecycle.lifecycleScope
import com.foxminded.android.locationtrackerkotlin.signin.SignInCommonFragment
import com.foxminded.android.locationtrackerkotlin.signin.SignInViewModel
import com.foxminded.android.locationtrackerkotlin.state.State
import com.foxminded.android.trackerviewer.databinding.FragmentSignInBinding
import com.foxminded.android.trackerviewer.di.config.App
import com.foxminded.android.trackerviewer.maps.MapsFragment
import com.foxminded.android.trackerviewer.phoneauth.PhoneAuthFragment
import com.foxminded.android.trackerviewer.signup.SignUpFragment
import javax.inject.Inject

class SignInFragment : SignInCommonFragment() {

    private val TAG = SignInFragment::class.java.simpleName
    private lateinit var binding: FragmentSignInBinding
    private lateinit var progressBar: ConstraintLayout
    private lateinit var emailResetPasswordEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var yourAccountTextView: TextView
    private lateinit var signInButton: Button
    private lateinit var resetPassword: Button
    private lateinit var forgotPasswordGroup: Group
    private lateinit var signInGroup: Group
    private lateinit var showAccountInfoGroup: Group

    @Inject
    lateinit var viewModel: SignInViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity?.application as App).mainComponent.injectSignInFragment(this)
        viewModel.requestAccountInfo()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        initViewsForCommonFragment()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.loginFragmentCommon.signInButton.setOnClickListener { viewModel.signIn() }
        binding.loginFragmentCommon.resetPasswordButton.setOnClickListener { viewModel.passwordReset() }
        binding.loginFragmentCommon.phoneButton.setOnClickListener { displayPhoneAuthFragment() }
        binding.loginFragmentCommon.continueButton.setOnClickListener { displayMapsFragment() }
        binding.loginFragmentCommon.signUpTextView.setOnClickListener { displaySignUpFragment() }
        binding.loginFragmentCommon.logoutButton.setOnClickListener { viewModel.signOut() }
        binding.loginFragmentCommon.forgotPasswordTextView.setOnClickListener {
            forgotPassword(forgotPasswordGroup, signInGroup)
        }
        binding.loginFragmentCommon.backResetPasswordButton.setOnClickListener {
            backButton(forgotPasswordGroup, signInGroup, emailResetPasswordEditText)
        }
        checkTextFields()
        checkState()
    }

    private fun checkState() {
        lifecycleScope.launchWhenStarted {
            viewModel.signInState.collect {
                when (it) {
                    is State.SucceededState -> {
                        hideProgressIndicator(progressBar)
                        viewModel.requestAccountInfo()
                    }
                    is State.ProgressIndicatorState -> {
                        showProgressIndicator(progressBar)
                    }
                    is State.ErrorState -> {
                        showToastMessage(it.message)
                        hideProgressIndicator(progressBar)
                    }
                    is State.DefaultState -> {
                        signOutButton(signInGroup, showAccountInfoGroup)
                    }
                    is State.AccountInfoState -> {
                        continueOrSignOut(it.accountInfo,
                            yourAccountTextView,
                            showAccountInfoGroup,
                            signInGroup)
                    }
                    is State.PasswordResetState -> {
                        hideProgressIndicator(progressBar)
                        returnSignInView(forgotPasswordGroup, signInGroup)
                        showResetPasswordMessage(it.email)
                    }
                }
            }
        }
    }

    private fun checkTextFields() {

        lifecycleScope.launchWhenStarted {
            textFieldEmailListener(emailEditText).collect {
                viewModel.requestEmailFromUser(it)
            }
        }

        lifecycleScope.launchWhenStarted {
            textFieldPasswordListener(passwordEditText, signInButton).collect {
                viewModel.requestPasswordFromUser(it)
            }
        }

        lifecycleScope.launchWhenStarted {
            textFieldResetPasswordListener(emailResetPasswordEditText, resetPassword).collect {
                viewModel.requestPasswordFromUser(it)
            }
        }
    }

    private fun initViewsForCommonFragment() {
        progressBar = binding.loginFragmentCommon.progressBarId.commonPb
        emailResetPasswordEditText = binding.loginFragmentCommon.emailResetPasswordEditText
        emailEditText = binding.loginFragmentCommon.emailEditText
        passwordEditText = binding.loginFragmentCommon.passwordEditText
        yourAccountTextView = binding.loginFragmentCommon.yourAccountTextView
        signInButton = binding.loginFragmentCommon.signInButton
        resetPassword = binding.loginFragmentCommon.resetPasswordButton
        forgotPasswordGroup = binding.loginFragmentCommon.forgotPasswordGroup
        signInGroup = binding.loginFragmentCommon.signInGroup
        showAccountInfoGroup = binding.loginFragmentCommon.showAccountInfoGroup
    }

    override fun onResume() {
        super.onResume()
        viewModel.requestAccountInfo()
    }

    private fun displayMapsFragment() {
        displayFragment(MapsFragment.newInstance())
    }

    private fun displayPhoneAuthFragment() {
        displayFragment(PhoneAuthFragment.newInstance())
    }

    private fun displaySignUpFragment() {
        displayFragment(SignUpFragment.newInstance())
    }

    companion object {
        fun newInstance() = SignInFragment()
    }

}