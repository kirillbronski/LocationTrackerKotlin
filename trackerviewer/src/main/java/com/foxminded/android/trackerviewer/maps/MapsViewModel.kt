package com.foxminded.android.trackerviewer.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foxminded.android.locationtrackerkotlin.extensions.buildMarker
import com.foxminded.android.locationtrackerkotlin.state.MapViewState
import com.foxminded.android.locationtrackerkotlin.utils.BaseResult
import com.foxminded.android.locationtrackerkotlin.utils.StateEnum.SIGN_OUT
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
    private val _mapViewState = MutableStateFlow<MapViewState>(MapViewState.DefaultState)
    val mapViewState: StateFlow<MapViewState> = _mapViewState.asStateFlow()

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
            _mapViewState.value = MapViewState.MarkerViewState(markers)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            mapsRepoImpl.signOut().run {
                when (this) {
                    is BaseResult.Success -> {
                        _mapViewState.value = MapViewState.SuccessState(SIGN_OUT.state)
                    }
                    is BaseResult.Error -> {
                        _mapViewState.value = MapViewState.ErrorState(this.errorMessage)
                    }
                }
            }
        }
    }
}