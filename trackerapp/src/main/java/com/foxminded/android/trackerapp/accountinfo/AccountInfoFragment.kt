package com.foxminded.android.trackerapp.accountinfo

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.foxminded.android.locationtrackerkotlin.accountinfo.AccountInfoViewModel
import com.foxminded.android.locationtrackerkotlin.state.ViewState
import com.foxminded.android.locationtrackerkotlin.utils.StateEnum.*
import com.foxminded.android.locationtrackerkotlin.view.BaseCommonFragment
import com.foxminded.android.trackerapp.R
import com.foxminded.android.trackerapp.databinding.FragmentAccountInfoBinding
import com.foxminded.android.trackerapp.di.config.App
import javax.inject.Inject

class AccountInfoFragment : BaseCommonFragment() {

    private val TAG = AccountInfoFragment::class.java.simpleName

    private val args: AccountInfoFragmentArgs by navArgs()

    private lateinit var binding: FragmentAccountInfoBinding
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.requestAccountInfo()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAccountInfoBinding.inflate(inflater, container, false)
        initBindingViews()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

    private fun initBindingViews() {
        progressBar = binding.accountInfoCommon.progressBarId.commonPb
        continueButton = binding.accountInfoCommon.continueButton
        logoutButton = binding.accountInfoCommon.logoutButton
        yourAccountTextView = binding.accountInfoCommon.yourAccountTextView
    }

    private fun displayMapsFragment() {
        findNavController().navigate(AccountInfoFragmentDirections
            .actionAccountInfoFragmentToMapsFragment(args.account))
    }

    private fun displaySignInFragment() {
        findNavController().navigate(R.id.action_accountInfoFragment_to_signInFragment)
    }
}