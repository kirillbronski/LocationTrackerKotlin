package com.foxminded.android.locationtrackerkotlin.accountinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foxminded.android.locationtrackerkotlin.state.BaseViewState
import com.foxminded.android.locationtrackerkotlin.utils.StateConst.ACCOUNT
import com.foxminded.android.locationtrackerkotlin.utils.StateConst.SIGN_OUT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AccountInfoViewModel(
    private val accountInfoRepoImpl: IAccountInfoRepo,
) : ViewModel() {

    private val _viewState = MutableStateFlow<BaseViewState>(BaseViewState.DefaultState)
    val viewState: StateFlow<BaseViewState> = _viewState.asStateFlow()

    fun requestAccountInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            val userInfo = accountInfoRepoImpl.currentFirebaseUser()
            _viewState.value = if (userInfo != null) {
                BaseViewState.SuccessState(state = ACCOUNT.state, stringValue = userInfo)
            } else {
                BaseViewState.ErrorState("Please sign in or sign up")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch(Dispatchers.IO) {
            if (accountInfoRepoImpl.signOut()) {
                _viewState.value =
                    BaseViewState.SuccessState(state = SIGN_OUT.state,
                        stringValue = "Sign Out Success")
            }
        }
    }
}