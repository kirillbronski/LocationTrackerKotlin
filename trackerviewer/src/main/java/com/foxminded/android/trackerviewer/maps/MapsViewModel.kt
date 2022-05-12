package com.foxminded.android.trackerviewer.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foxminded.android.locationtrackerkotlin.firestoreuser.User
import com.foxminded.android.locationtrackerkotlin.extensions.buildMarkers
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapsViewModel(
    private var mapsRepoImpl: IMapsRepo,
) : ViewModel() {

    private val TAG = MapsViewModel::class.java.simpleName
    private val _markerOptions = MutableStateFlow<List<MarkerOptions>>(mutableListOf())
    val markerOptions: StateFlow<List<MarkerOptions>> = _markerOptions.asStateFlow()

    init {
        getDataFromFirestore(null)
    }

    fun getDataFromFirestore(date: String?) {
        val markers = mutableListOf<MarkerOptions>()
        viewModelScope.launch {
            mapsRepoImpl.getDataFromFirestore().forEach {
                if (date == null || date == it.dateAndTime) {
                    markers.add(User().buildMarkers(
                        it.latitude,
                        it.longitude,
                        it.accountInfo,
                        it.dateAndTime))
                }
            }
            _markerOptions.value = markers
        }
    }
}