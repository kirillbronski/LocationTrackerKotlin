package com.foxminded.android.trackerapp.maps

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.foxminded.android.locationtrackerkotlin.state.MapViewState
import com.foxminded.android.locationtrackerkotlin.state.ViewState
import com.foxminded.android.locationtrackerkotlin.utils.StateEnum.DEFAULT
import com.foxminded.android.locationtrackerkotlin.utils.StateEnum.SIGN_OUT
import com.foxminded.android.locationtrackerkotlin.view.BaseFragment
import com.foxminded.android.trackerapp.R
import com.foxminded.android.trackerapp.databinding.FragmentMapsBinding
import com.foxminded.android.trackerapp.di.config.App
import com.foxminded.android.trackerapp.services.TrackerService
import com.foxminded.android.trackerapp.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import javax.inject.Inject

class MapsFragment : BaseFragment<FragmentMapsBinding>() {

    private val TAG = MapsFragment::class.java.simpleName
    private val args: MapsFragmentArgs by navArgs()

    @Inject
    lateinit var viewModel: MapsViewModel

    private val enableGpsSettings =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    private val callback = OnMapReadyCallback { map ->
        settingsMap(map)
        checkViewState()
        checkMapState()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity?.application as App).mainComponent.injectMapsFragment(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        viewModel.requestAccountInfo(args.account)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
    }

    override fun onResume() {
        super.onResume()
        isMapsEnabled()
        viewModel.requestLocation()
    }

    @SuppressLint("MissingPermission")
    private fun settingsMap(map: GoogleMap) {
        try {
            map.isMyLocationEnabled = true
        } catch (e: SecurityException) {
            Log.e(TAG, "error: " + e.message.toString(), e)
        }
        map.isTrafficEnabled = true
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.setAllGesturesEnabled(true)
    }

    private fun sendCommandToService(action: String) =
        Intent(requireContext(), TrackerService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    private fun checkViewState() {
        lifecycleScope.launchWhenStarted {
            viewModel.mapsState.collect {
                when (it) {
                    is ViewState.SuccessState -> {
                        when (it.state) {
                            SIGN_OUT.state -> {
                                findNavController().navigate(R.id.action_mapsFragment_to_signInFragment)
                                it.state = DEFAULT.state
                            }
                        }
                    }
                    is ViewState.ErrorState -> {
                        showToastMessage(it.message)
                        it.message = null
                    }
                    else -> {}
                }
            }
        }
    }

    private fun checkMapState() {
        lifecycleScope.launchWhenStarted {
            viewModel.mapsLocationState.collect {
                when (it) {
                    is MapViewState.ViewStateLocationListener -> {
                        showToastMessage(it.message)
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sign_out_menu -> {
                viewModel.signOut()
                return true
            }
            R.id.close_app_menu -> {
                requireActivity().finish()
                showToastMessage(getString(R.string.app_is_closed_menu))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun isMapsEnabled(): Boolean {
        val locationManager: LocationManager =
            context?.getSystemService(Context.LOCATION_SERVICE) as (LocationManager)
        return if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showAlertMessageNoGps()
            false
        } else {
            true
        }
    }

    private fun showAlertMessageNoGps() =
        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.requires_gps))
            .setCancelable(false)
            .setPositiveButton("Enable GPS") { _, _ ->
                val enableGpsIntent =
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                enableGpsSettings.launch(enableGpsIntent)
            }
            .create()
            .show()

    override fun getViewBinding() = FragmentMapsBinding.inflate(layoutInflater)
}