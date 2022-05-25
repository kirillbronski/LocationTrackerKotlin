package com.foxminded.android.locationtrackerkotlin.accountinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foxminded.android.locationtrackerkotlin.state.BaseViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val ACCOUNT = 1
private const val SIGN_OUT = 2

class AccountInfoViewModel(
    private val accountInfoRepoImpl: IAccountInfoRepo,
) : ViewModel() {

    private val _viewState = MutableStateFlow<BaseViewState>(BaseViewState.DefaultState)
    val viewState: StateFlow<BaseViewState> = _viewState.asStateFlow()

    fun requestAccountInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            val userInfo = accountInfoRepoImpl.currentFirebaseUser()
            if (userInfo != null) {
                _viewState.value =
                    BaseViewState.SuccessState(state = ACCOUNT, stringValue = userInfo)
            } else {
                _viewState.value = BaseViewState.ErrorState("Please sign in or sign up")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch(Dispatchers.IO) {
            if (accountInfoRepoImpl.signOut()) {
                _viewState.value =
                    BaseViewState.SuccessState(state = SIGN_OUT, stringValue = "Sign Out Success")
            }
        }
    }
}