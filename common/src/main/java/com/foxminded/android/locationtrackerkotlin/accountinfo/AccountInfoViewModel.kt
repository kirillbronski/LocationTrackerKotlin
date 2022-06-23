package com.foxminded.android.locationtrackerkotlin.accountinfo

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foxminded.android.locationtrackerkotlin.state.ViewState
import com.foxminded.android.locationtrackerkotlin.utils.BaseResult
import com.foxminded.android.locationtrackerkotlin.utils.StateEnum.ACCOUNT
import com.foxminded.android.locationtrackerkotlin.utils.StateEnum.SIGN_OUT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AccountInfoViewModel(
    private val accountInfoRepoImpl: IAccountInfoRepo,
) : ViewModel() {

    private val TAG = AccountInfoViewModel::class.java.simpleName

    private val _viewState = MutableStateFlow<ViewState>(ViewState.DefaultState)
    val viewState: StateFlow<ViewState> = _viewState.asStateFlow()

    fun requestAccountInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            accountInfoRepoImpl.currentFirebaseUser().run {
                _viewState.value = if (this != null) {
                    ViewState.SuccessState(state = ACCOUNT.state,
                        stringValue = this)
                } else {
                    ViewState.ErrorState("Please sign in or sign up")
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            accountInfoRepoImpl.signOut().run {
                when (this) {
                    is BaseResult.Success -> {
                        _viewState.value = ViewState.SuccessState(SIGN_OUT.state, "Sign out!")
                    }
                    is BaseResult.Error -> {
                        Log.d(TAG, "signOut() returned: ${this.errorMessage}")
                        _viewState.value = ViewState.ErrorState(this.errorMessage)
                    }
                }
            }
        }
    }
}