package com.foxminded.android.trackerviewer.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foxminded.android.locationtrackerkotlin.extensions.buildMarker
import com.foxminded.android.locationtrackerkotlin.state.MapsState
import com.foxminded.android.locationtrackerkotlin.utils.BaseResult
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapsViewModel(
    private var mapsRepoImpl: IMapsRepo,
) : ViewModel() {

    private val TAG = MapsViewModel::class.java.simpleName
    private val _mapsState = MutableStateFlow<MapsState>(MapsState.DefaultState)
    val mapsState: StateFlow<MapsState> = _mapsState.asStateFlow()

    init {
        getDataFromFirestore(null)
    }

    fun getDataFromFirestore(date: String?) {
        val markers = mutableListOf<MarkerOptions>()
        viewModelScope.launch(Dispatchers.IO) {
            mapsRepoImpl.getDataFromFirestore()?.forEach {
                if (date == null || (it.dateAndTime?.contains(date) ?: it.dateAndTime) as Boolean) {
                    markers.add(buildMarker(it))
                }
            }
            _mapsState.value = MapsState.MarkerState(markers)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            mapsRepoImpl.signOut().run {
                when (this) {
                    is BaseResult.Success -> {
                        _mapsState.value = MapsState.SuccessState
                    }
                    is BaseResult.Error -> {
                        _mapsState.value = MapsState.ErrorState(this.errorMessage)
                    }
                }
            }
        }
    }
}