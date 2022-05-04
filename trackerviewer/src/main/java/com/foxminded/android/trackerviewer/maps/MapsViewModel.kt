package com.foxminded.android.trackerviewer.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MapsViewModel(
    private var mapsRepoImpl: IMapsRepo,
) : ViewModel() {

    private val TAG = MapsViewModel::class.java.simpleName
    private val scope = CoroutineScope(Dispatchers.IO)
    private val markers = mutableListOf<MarkerOptions>()
    private val _markerOptions = MutableLiveData<List<MarkerOptions>>()
    val markerOptions: LiveData<List<MarkerOptions>> = _markerOptions

    init {
        getDataFromFirestore(null)
    }

    fun getDataFromFirestore(date: String?) {
        markers.clear()
        scope.launch {
            mapsRepoImpl.getDataFromFirestore().collect {
                for (i in it.indices) {
                    if (date != null && date == it[i].dateAndTime) {
                        markers.add(getMarkers(
                            it[i].latitude, it[i].longitude, it[i].accountInfo, it[i].dateAndTime))
                    }
                    if (date == null) {
                        markers.add(getMarkers(
                            it[i].latitude, it[i].longitude, it[i].accountInfo, it[i].dateAndTime))
                    }
                }
                _markerOptions.postValue(markers)
            }
        }
    }

    private fun getMarkers(
        latitude: Double, longitude: Double,
        accountInfo: String?, dateAndTime: String?,
    ): MarkerOptions {
        return MarkerOptions()
            .position(LatLng(latitude, longitude))
            .title(accountInfo)
            .snippet(dateAndTime)
    }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
}