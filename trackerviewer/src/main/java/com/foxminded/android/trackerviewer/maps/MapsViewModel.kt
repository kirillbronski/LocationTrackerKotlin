package com.foxminded.android.trackerviewer.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foxminded.android.locationtrackerkotlin.extensions.buildMarker
import com.foxminded.android.locationtrackerkotlin.state.MapsState
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
                if (date == null || date == it.dateAndTime) {
                    markers.add(buildMarker(it))
                }
            }
            _mapsState.value = MapsState.MarkerState(markers)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            if (mapsRepoImpl.signOut()) {
                _mapsState.value = MapsState.DefaultState
            }
        }
    }
}