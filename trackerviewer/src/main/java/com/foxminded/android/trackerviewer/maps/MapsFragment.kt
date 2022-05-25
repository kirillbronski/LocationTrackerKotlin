package com.foxminded.android.trackerviewer.maps

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.DatePickerDialog
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
import com.foxminded.android.locationtrackerkotlin.state.MapsState
import com.foxminded.android.trackerviewer.R
import com.foxminded.android.trackerviewer.databinding.FragmentMapsBinding
import com.foxminded.android.trackerviewer.di.config.App
import com.foxminded.android.trackerviewer.signin.SignInFragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import java.util.*
import javax.inject.Inject

class MapsFragment : Fragment() {

    private val TAG = MapsFragment::class.java.simpleName
    private lateinit var binding: FragmentMapsBinding

    @Inject
    lateinit var viewModel: MapsViewModel

    private val callback = OnMapReadyCallback { map ->

        settingsMap(map)

        binding.chipMap.setOnCloseIconClickListener {
            map.clear()
            it.visibility = View.GONE
            viewModel.getDataFromFirestore(date = null)
        }
        checkState(map)

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
        permissionsRequestLauncher.launch(arrayOf(ACCESS_FINE_LOCATION, WRITE_EXTERNAL_STORAGE))
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun checkState(map: GoogleMap) {
        lifecycleScope.launchWhenStarted {
            viewModel.mapsState.collect {
                when (it) {
                    is MapsState.MarkerState -> {
                        map.clear()
                        it.markers.forEach { marker ->
                            map.addMarker(marker)
                        }
                    }
                    is MapsState.DefaultState -> {
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit_calendar -> {
                initCalendar()
                return true
            }
            R.id.sign_out_menu -> {
                viewModel.signOut()
                return true
            }
            R.id.close_app -> {
                closeApp()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SetTextI18n")
    private fun initCalendar() {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]
        DatePickerDialog(
            requireContext(),
            { _, year1, month1, dayOfMonth1 ->
                val correctMonth = month1 + 1
                viewModel.getDataFromFirestore(date = "$year1-$correctMonth-$dayOfMonth1")
                binding.chipMap.text = "$year1-$correctMonth-$dayOfMonth1"
                binding.chipMap.visibility = View.VISIBLE
            }, year, month, dayOfMonth
        ).show()
    }

    private fun closeApp() {
        requireActivity().finish()
        Toast.makeText(
            requireActivity(),
            getString(R.string.app_is_closed_menu),
            Toast.LENGTH_SHORT
        ).show()
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
