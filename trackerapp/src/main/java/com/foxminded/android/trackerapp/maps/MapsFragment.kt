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
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.foxminded.android.locationtrackerkotlin.state.State
import com.foxminded.android.trackerapp.R
import com.foxminded.android.trackerapp.databinding.FragmentMapsBinding
import com.foxminded.android.trackerapp.di.config.App
import com.foxminded.android.trackerapp.signin.SignInFragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import javax.inject.Inject

class MapsFragment : Fragment() {

    private val TAG = MapsFragment::class.java.simpleName
    private lateinit var binding: FragmentMapsBinding

    @Inject
    lateinit var viewModel: MapsViewModel

    private val callback = OnMapReadyCallback { map ->
        settingsMap(map)
        lifecycleScope.launchWhenStarted {
            viewModel.mapsState.collect {
                when (it) {
                    is State.ErrorState -> {
                        showToastMessage(it.message)
                    }
                    is State.StateLocationListener -> {
                        showToastMessage(it.message)
                    }
                    is State.SignOut -> {
                        SignInFragment.newInstance()
                    }
                    else -> {}
                }

            }
        }
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

    private fun showToastMessage(text: String?) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun newInstance(): MapsFragment {
            return MapsFragment()
        }
    }

    private val permissionsRequestLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
        ::onGotPermissionsResult
    )

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

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        isMapsEnabled()
    }

    private fun isMapsEnabled(): Boolean {
        val locationManager: LocationManager =
            context?.getSystemService(Context.LOCATION_SERVICE) as (LocationManager)
        return if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
            false
        } else {
            true
        }
    }

    private fun buildAlertMessageNoGps() {
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

    private val enableGpsSettings =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ -> }
}