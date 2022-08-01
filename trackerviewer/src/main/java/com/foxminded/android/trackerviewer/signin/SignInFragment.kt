package com.foxminded.android.trackerviewer.signin

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.foxminded.android.locationtrackerkotlin.extensions.textFieldListener
import com.foxminded.android.locationtrackerkotlin.signin.SignInViewModel
import com.foxminded.android.locationtrackerkotlin.state.ViewState
import com.foxminded.android.locationtrackerkotlin.utils.StateEnum.*
import com.foxminded.android.locationtrackerkotlin.view.BaseFragment
import com.foxminded.android.trackerviewer.R
import com.foxminded.android.trackerviewer.databinding.FragmentSignInBinding
import com.foxminded.android.trackerviewer.di.config.App
import javax.inject.Inject

class SignInFragment : BaseFragment<FragmentSignInBinding>() {

    private val TAG = SignInFragment::class.java.simpleName
    private lateinit var progressBar: ConstraintLayout
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signInButton: Button

    private val permissionsRequestLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
        ::onGotPermissionsResult
    )

    @Inject
    lateinit var viewModel: SignInViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity?.application as App).mainComponent.injectSignInFragment(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionsRequestLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE))
        viewModel.requestAccountInfo()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBindingViews()
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

    private fun onGotPermissionsResult(grantResults: Map<String, Boolean>) {
        if (grantResults.entries.all { it.value }) {
            Toast.makeText(
                requireContext(),
                R.string.all_permission_granted,
                Toast.LENGTH_SHORT
            ).show()
        } else {
            askUserForOpenInAppSettings()
        }
    }

    private fun askUserForOpenInAppSettings() {
        val appSettingsIntent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", activity?.packageName?.toString(), null)
        )

        activity?.packageManager?.resolveActivity(
            appSettingsIntent,
            PackageManager.MATCH_DEFAULT_ONLY
        )?.let {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.permission_denied)
                .setMessage(R.string.permission_denied_forever_message)
                .setCancelable(false)
                .setPositiveButton("Open") { _, _ ->
                    startActivity(appSettingsIntent)
                }
                .create()
                .show()
        }
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
                viewModel.checkEmailAndPasswordFieldsValue(email1 = it)
            }
        }

        lifecycleScope.launchWhenStarted {
            textFieldListener(passwordEditText).collect {
                viewModel.checkEmailAndPasswordFieldsValue(password1 = it)
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

    override fun getViewBinding() = FragmentSignInBinding.inflate(layoutInflater)
}