package com.foxminded.android.trackerapp.maps

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.foxminded.android.locationtrackerkotlin.extensions.set
import com.foxminded.android.locationtrackerkotlin.firestoreuser.User
import com.foxminded.android.locationtrackerkotlin.state.State
import com.foxminded.android.trackerapp.utils.DataConvert
import com.foxminded.android.trackerapp.utils.IConfigApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapsViewModel(
    private val mapsRepoImpl: IMapsRepo,
    private val mapsRepoFirestoreImpl: IMapsRepoFirestore,
    private val locationManager: LocationManager,
    private val connectivityManager: ConnectivityManager,
    private val periodicWorkRequest: PeriodicWorkRequest,
    private val workManager: WorkManager,
    private val configApp: IConfigApp,
) : ViewModel() {

    private val TAG = MapsViewModel::class.java.simpleName
    private val _mapsState = MutableStateFlow<State>(State.DefaultState)
    val mapsState: StateFlow<State> = _mapsState.asStateFlow()

    private lateinit var accountInfo: String

    init {
        Log.d(TAG, "init RUN: ")
        viewModelScope.launch {
            accountInfo = mapsRepoFirestoreImpl.currentFirebaseUser().toString()
        }
        Log.d(TAG, "init RUN: $accountInfo")
        startWorkManagerTask()
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            if (isOnline()) {
                insertDataToFirestoreSilently(location)
                Log.d(TAG, "onLocationChanged: ONLINE")
            } else {
                insertDataToRoomTableSilently(location)
                Log.d(TAG, "onLocationChanged: OFFLINE")
            }
        }

        override fun onProviderDisabled(provider: String) {
            Log.d(TAG, "onProviderDisabled: $provider")
        }

        override fun onProviderEnabled(provider: String) {
            Log.d(TAG, "onProviderEnabled: $provider")
        }
    }

    @SuppressLint("MissingPermission")
    fun requestLocation() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
            configApp.requestTime(), configApp.requestDistance(), locationListener)
    }

    @SuppressLint("MissingPermission")
    private fun isOnline(): Boolean {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i(TAG, "isOnline: NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i(TAG, "isOnline: NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i(TAG, "isOnline: NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
        return false
    }

    fun insertDataToFirestoreSilently(location: Location) {

        val user = User(accountInfo,
            location.latitude,
            location.longitude,
            DataConvert.dateToStringFormat(location.time))

        viewModelScope.launch {
            try {
                mapsRepoFirestoreImpl.insertDataToFirestore(user)
            } catch (e: Exception) {
                Log.d(TAG, "insertDataToFirestoreSilently() returned: ${e.message}")
                _mapsState.set(State.ErrorState(e.message.toString()))
            }
        }
    }

    fun insertDataToRoomTableSilently(location: Location) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                mapsRepoImpl.insertInTable(accountInfo,
                    location.latitude,
                    location.longitude,
                    DataConvert.dateToStringFormat(location.time))
            } catch (e: Exception) {
                Log.d(TAG, "insertDataToRoomTableSilently() returned: ${e.message}")
                _mapsState.set(State.ErrorState(e.message.toString()))
            }
        }
    }

    fun deleteAllDataFromTable() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = mapsRepoImpl.deleteAllDataFromTable()
                Log.d(TAG, "deleteAllDataFromTable: $result")
            } catch (e: Exception) {
                Log.d(TAG, "deleteAllDataFromTable() returned: ${e.message}")
                _mapsState.set(State.ErrorState(e.message.toString()))
            }
        }
    }

    fun stopUpdateGps() {
        locationManager.removeUpdates(locationListener)
        _mapsState.set(State.StateLocationListener("Location listener was stopped"))
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                mapsRepoFirestoreImpl.signOut()
                _mapsState.set(State.SignOut)
            } catch (e: Exception) {
                Log.d(TAG, "signOut() returned: ${e.message}")
                _mapsState.set(State.ErrorState(e.message.toString()))
            }
        }
    }

    private fun startWorkManagerTask() {
        workManager.enqueue(periodicWorkRequest)
    }

    private fun cancelWorkManagerTask() {
        workManager.cancelWorkById(periodicWorkRequest.id)
    }

    override fun onCleared() {
        super.onCleared()
        cancelWorkManagerTask()
    }
}