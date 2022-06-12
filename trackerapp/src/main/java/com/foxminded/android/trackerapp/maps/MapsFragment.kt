package com.foxminded.android.trackerapp.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.foxminded.android.locationtrackerkotlin.state.ViewState
import com.foxminded.android.locationtrackerkotlin.state.MapsState
import com.foxminded.android.locationtrackerkotlin.view.BaseCommonFragment
import com.foxminded.android.trackerapp.R
import com.foxminded.android.trackerapp.databinding.FragmentMapsBinding
import com.foxminded.android.trackerapp.di.config.App
import com.foxminded.android.trackerapp.signin.SignInFragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import javax.inject.Inject

private const val ACCOUNT_INFO = "ACCOUNT_INFO"

class MapsFragment : BaseCommonFragment() {

    companion object {
        fun newInstance(accountInfo: String?) =
            MapsFragment().apply {
                arguments = Bundle().apply {
                    putString(ACCOUNT_INFO, accountInfo)
                }
            }
    }

    private val TAG = MapsFragment::class.java.simpleName
    private lateinit var binding: FragmentMapsBinding

    @Inject
    lateinit var viewModel: MapsViewModel

    private val enableGpsSettings =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ -> }

    private val permissionsRequestLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
        ::onGotPermissionsResult
    )

    private val callback = OnMapReadyCallback { map ->
        setUpMap(map)
        checkViewState()
        checkMapState()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity?.application as App).mainComponent.injectMapsFragment(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        setHasOptionsMenu(true)
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        viewModel.getValueFromBundle(arguments?.get(ACCOUNT_INFO) as String?)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        permissionsRequestLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE))
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        viewModel.requestLocation()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        isMapsEnabled()
    }

    @SuppressLint("MissingPermission")
    private fun setUpMap(map: GoogleMap) {
        try {
            map.isMyLocationEnabled = true
        } catch (e: SecurityException) {
            Log.e(TAG, "error: " + e.message.toString(), e)
        }
        map.isTrafficEnabled = true
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.setAllGesturesEnabled(true)
    }

    private fun checkViewState() {
        lifecycleScope.launchWhenStarted {
            viewModel.mapsState.collect {
                when (it) {
                    is ViewState.SuccessState -> {
                        displayFragment(SignInFragment.newInstance())
                    }
                    is ViewState.ErrorState -> {
                        showToastMessage(it.message)
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
                    is MapsState.StateLocationListener -> {
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
            R.id.stop_update_gps -> {
                viewModel.stopUpdateGps()
                return true
            }
            R.id.run_update_gps -> {
                viewModel.requestLocation()
                return true
            }
            R.id.delete_all_data -> {
                viewModel.deleteAllDataFromTable()
                return true
            }
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

    private fun onGotPermissionsResult(grantResults: Map<String, Boolean>) {
        if (grantResults.entries.all { it.value }) {
            Toast.makeText(
                requireContext(),
                R.string.all_permission_granted,
                Toast.LENGTH_SHORT
            ).show()
        } else {
            askUserForOpenInAppSettings()
        }
    }

    private fun askUserForOpenInAppSettings() {
        val appSettingsIntent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", activity?.packageName?.toString(), null)
        )

        activity?.packageManager?.resolveActivity(
            appSettingsIntent,
            PackageManager.MATCH_DEFAULT_ONLY
        )?.let {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.permission_denied)
                .setMessage(R.string.permission_denied_forever_message)
                .setCancelable(false)
                .setPositiveButton("Open") { _, _ ->
                    startActivity(appSettingsIntent)
                }
                .create()
                .show()
        }
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
}