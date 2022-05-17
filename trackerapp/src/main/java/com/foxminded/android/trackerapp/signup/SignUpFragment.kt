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
import com.foxminded.android.locationtrackerkotlin.signup.SignUpCommonFragment
import com.foxminded.android.locationtrackerkotlin.signup.SignUpViewModel
import com.foxminded.android.locationtrackerkotlin.state.State
import com.foxminded.android.trackerapp.databinding.FragmentSignUpBinding
import com.foxminded.android.trackerapp.di.config.App
import com.foxminded.android.trackerapp.signin.SignInFragment
import javax.inject.Inject

class SignUpFragment : SignUpCommonFragment() {

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
        initViewsForCommonFragment()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signUpCommon.signUpButton.setOnClickListener {
            createAccount()
        }
        checkTextFields()
        checkState()
    }

    private fun createAccount() {
        viewModel.createAccount()
    }

    private fun checkState() {
        lifecycleScope.launchWhenStarted {
            viewModel.signUpState.collect {
                when (it) {
                    is State.SucceededState -> {
                        hideProgressIndicator(progressBar)
                        displayAccountInfo()
                    }
                    is State.ProgressIndicatorState -> {
                        showProgressIndicator(progressBar)
                    }
                    is State.ErrorState -> {
                        showToastMessage(it.message)
                        hideProgressIndicator(progressBar)
                    }
                    is State.PasswordResetState -> {
                        hideProgressIndicator(progressBar)
                    }
                    else -> {}
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
            textFieldPasswordListener(passwordEditText).collect {
                viewModel.requestPasswordFromUser(it)
            }
        }

        lifecycleScope.launchWhenStarted {
            textFieldPasswordAgainListener(passwordAgainEditText, signUpButton).collect {
                viewModel.requestPasswordAgainFromUser(it)
            }
        }
    }

    private fun initViewsForCommonFragment() {
        progressBar = binding.signUpCommon.progressBarId.commonPb
        emailEditText = binding.signUpCommon.emailEditText
        passwordEditText = binding.signUpCommon.passwordEditText
        passwordAgainEditText = binding.signUpCommon.passwordAgainEditText
        signUpButton = binding.signUpCommon.signUpButton
    }

    private fun displayAccountInfo() {
        displayFragment(SignInFragment.newInstance())
    }

    companion object {
        fun newInstance() = SignUpFragment()
    }
}