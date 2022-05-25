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
import com.foxminded.android.locationtrackerkotlin.firestoreuser.User
import com.foxminded.android.locationtrackerkotlin.state.BaseViewState
import com.foxminded.android.locationtrackerkotlin.state.MapsState
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
    private val _mapsState = MutableStateFlow<BaseViewState>(BaseViewState.DefaultState)
    val mapsState: StateFlow<BaseViewState> = _mapsState.asStateFlow()
    private val _mapsLocationState = MutableStateFlow<MapsState>(MapsState.DefaultState)
    val mapsLocationState: StateFlow<MapsState> = _mapsLocationState.asStateFlow()

    private lateinit var accountInfoFromServer: String
    private var accountInfoFromBundle: String? = null

    fun getValueFromBundle(accountInfoFromBundle: String?): String? {
        return accountInfoFromBundle.also { this.accountInfoFromBundle = it }
    }


    init {
        viewModelScope.launch {
            accountInfoFromServer = mapsRepoFirestoreImpl.currentFirebaseUser().toString()
        }
        startWorkManagerTask()
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            if (isOnline()) {
                if (accountInfoFromBundle != null) {
                    insertDataToFirestoreSilently(location, accountInfoFromBundle)
                } else {
                    insertDataToFirestoreSilently(location, accountInfoFromServer)
                }
                Log.d(TAG, "onLocationChanged: ONLINE")
            } else {
                if (accountInfoFromBundle != null) {
                    insertDataToRoomTableSilently(location, accountInfoFromBundle)
                } else {
                    insertDataToRoomTableSilently(location, accountInfoFromServer)
                }
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

    fun insertDataToFirestoreSilently(location: Location, account: String?) {

        val user = User(account,
            location.latitude,
            location.longitude,
            DataConvert.dateToStringFormat(location.time))

        viewModelScope.launch {
            try {
                mapsRepoFirestoreImpl.insertDataToFirestore(user)
            } catch (e: Exception) {
                Log.d(TAG, "insertDataToFirestoreSilently() returned: ${e.message}")
                _mapsState.value = BaseViewState.ErrorState(e.message.toString())
            }
        }
    }

    fun insertDataToRoomTableSilently(location: Location, account: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (account != null) {
                    mapsRepoImpl.insertInTable(account,
                        location.latitude,
                        location.longitude,
                        DataConvert.dateToStringFormat(location.time))
                }
            } catch (e: Exception) {
                Log.d(TAG, "insertDataToRoomTableSilently() returned: ${e.message}")
                _mapsState.value = BaseViewState.ErrorState(e.message.toString())
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
                _mapsState.value = BaseViewState.ErrorState(e.message.toString())
            }
        }
    }

    fun stopUpdateGps() {
        locationManager.removeUpdates(locationListener)
        _mapsLocationState.value = MapsState.StateLocationListener("Location listener was stopped")
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                mapsRepoFirestoreImpl.signOut()
                _mapsState.value = BaseViewState.DefaultState
            } catch (e: Exception) {
                Log.d(TAG, "signOut() returned: ${e.message}")
                _mapsState.value = BaseViewState.ErrorState(e.message.toString())
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