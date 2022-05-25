package com.foxminded.android.locationtrackerkotlin.state

import com.google.android.gms.maps.model.MarkerOptions

sealed class MapsState {
    object DefaultState : MapsState()
    class StateLocationListener(val message: String) : MapsState()
    class MarkerState(val markers: MutableCollection<MarkerOptions>) : MapsState()
}
