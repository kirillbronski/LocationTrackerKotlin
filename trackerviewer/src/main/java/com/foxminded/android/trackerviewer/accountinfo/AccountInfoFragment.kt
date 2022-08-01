package com.foxminded.android.trackerviewer.accountinfo

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.foxminded.android.locationtrackerkotlin.accountinfo.AccountInfoViewModel
import com.foxminded.android.locationtrackerkotlin.state.ViewState
import com.foxminded.android.locationtrackerkotlin.utils.StateEnum.*
import com.foxminded.android.locationtrackerkotlin.view.BaseFragment
import com.foxminded.android.trackerviewer.R
import com.foxminded.android.trackerviewer.databinding.FragmentAccountInfoBinding
import com.foxminded.android.trackerviewer.di.config.App
import javax.inject.Inject

class AccountInfoFragment : BaseFragment<FragmentAccountInfoBinding>() {

    private val TAG = AccountInfoFragment::class.java.simpleName

    private val args: AccountInfoFragmentArgs by navArgs()

    private lateinit var progressBar: ConstraintLayout
    private lateinit var continueButton: Button
    private lateinit var logoutButton: Button
    private lateinit var yourAccountTextView: TextView

    @Inject
    lateinit var viewModel: AccountInfoViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity?.application as App).mainComponent.injectAccountInfoFragment(this)
    }

    override fun onResume() {
        super.onResume()
        viewModel.requestAccountInfo()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBindingViews()
        replaceIconColor()
        continueButton.setOnClickListener {
            displayMapsFragment()
        }
        logoutButton.setOnClickListener {
            viewModel.signOut()
        }

        checkViewState(args.account)
    }

    @SuppressLint("SetTextI18n")
    private fun checkViewState(account: String?) {
        Log.d(TAG, "checkViewState: $account")
        lifecycleScope.launchWhenStarted {
            viewModel.viewState.collect {
                when (it) {
                    is ViewState.SuccessState -> {
                        when (it.state) {
                            ACCOUNT.state -> {
                                if (account != "default") {
                                    yourAccountTextView.text = account
                                    continueButton.isEnabled = true
                                    it.state = DEFAULT.state
                                } else {
                                    yourAccountTextView.text = it.stringValue
                                    continueButton.isEnabled = true
                                    it.state = DEFAULT.state
                                }
                            }
                            SIGN_OUT.state -> {
                                displaySignInFragment()
                                Log.d(TAG, "checkViewState: SIGN_OUT")
                                it.state = DEFAULT.state
                                it.state = DEFAULT.state
                            }
                        }
                    }
                    is ViewState.ErrorState -> {
                        showToastMessage(it.message)
                        displaySignInFragment()
                        it.message = null
                    }
                    else -> {}
                }
            }
        }
    }

    private fun replaceIconColor() {
        binding.accountInfoCommon.yourAccountTextView
            .setCompoundDrawablesRelativeWithIntrinsicBounds(
                com.foxminded.android.locationtrackerkotlin.R.drawable.ic_baseline_account_circle_viewer,
                0,
                0,
                0)
    }

    private fun initBindingViews() {
        progressBar = binding.accountInfoCommon.progressBarId.commonPb
        continueButton = binding.accountInfoCommon.continueButton
        logoutButton = binding.accountInfoCommon.logoutButton
        yourAccountTextView = binding.accountInfoCommon.yourAccountTextView
    }

    private fun displayMapsFragment() {
        findNavController().navigate(R.id.action_accountInfoFragment_to_mapsFragment)
    }

    private fun displaySignInFragment() {
        findNavController().navigate(R.id.action_accountInfoFragment_to_signInFragment)
    }

    override fun getViewBinding() = FragmentAccountInfoBinding.inflate(layoutInflater)
}