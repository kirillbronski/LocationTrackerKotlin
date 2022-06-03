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
import com.foxminded.android.locationtrackerkotlin.accountinfo.AccountInfoViewModel
import com.foxminded.android.locationtrackerkotlin.state.BaseViewState
import com.foxminded.android.locationtrackerkotlin.utils.StateConst.*
import com.foxminded.android.locationtrackerkotlin.view.BaseCommonFragment
import com.foxminded.android.trackerapp.databinding.FragmentAccountInfoBinding
import com.foxminded.android.trackerapp.di.config.App
import com.foxminded.android.trackerapp.maps.MapsFragment
import com.foxminded.android.trackerapp.signin.SignInFragment
import javax.inject.Inject

private const val ACCOUNT_INFO = "ACCOUNT_INFO"

class AccountInfoFragment : BaseCommonFragment() {

    private val TAG = AccountInfoFragment::class.java.simpleName

    private lateinit var binding: FragmentAccountInfoBinding
    private lateinit var progressBar: ConstraintLayout
    private lateinit var continueButton: Button
    private lateinit var logoutButton: Button
    private lateinit var yourAccountTextView: TextView

    @Inject
    lateinit var viewModel: AccountInfoViewModel

    companion object {
        fun newInstance(accountInfo: String?) =
            AccountInfoFragment().apply {
                arguments = Bundle().apply {
                    putString(ACCOUNT_INFO, accountInfo)
                }
            }
    }

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

        checkViewState()
    }

    @SuppressLint("SetTextI18n")
    private fun checkViewState() {
        val accountInfo = arguments?.get(ACCOUNT_INFO)
        Log.d(TAG, "checkViewState: $accountInfo")
        lifecycleScope.launchWhenStarted {
            viewModel.viewState.collect {
                when (it) {
                    is BaseViewState.SuccessState -> {
                        when (it.state) {
                            ACCOUNT.state -> {
                                if (accountInfo != null) {
                                    yourAccountTextView.text = "Your account: $accountInfo"
                                    continueButton.isEnabled = true
                                } else {
                                    yourAccountTextView.text = "Your account: " + it.stringValue
                                    continueButton.isEnabled = true
                                }
                            }
                            SIGN_OUT.state -> {
                                displaySignInFragment()
                                Log.d(TAG, "checkViewState: SIGN_OUT")
                                it.state = DEFAULT.state
                            }
                        }
                    }
                    is BaseViewState.ErrorState -> {
                        showToastMessage(it.message)
                        displaySignInFragment()
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
        displayFragment(MapsFragment.newInstance(arguments?.get(ACCOUNT_INFO) as String?))
    }

    private fun displaySignInFragment() {
        displayFragment(SignInFragment.newInstance())
    }
}