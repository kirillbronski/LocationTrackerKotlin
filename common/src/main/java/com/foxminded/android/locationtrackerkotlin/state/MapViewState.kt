package com.foxminded.android.locationtrackerkotlin.state

import com.google.android.gms.maps.model.MarkerOptions

sealed class MapViewState {
    object DefaultState : MapViewState()
    class SuccessState(var state: String?) : MapViewState()
    class ErrorState(var errorMessage: String?) : MapViewState()
    class ViewStateLocationListener(val message: String) : MapViewState()
    class MarkerViewState(val markers: MutableCollection<MarkerOptions>) : MapViewState()
}
