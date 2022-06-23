package com.foxminded.android.trackerviewer.maps

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.foxminded.android.locationtrackerkotlin.state.MapViewState
import com.foxminded.android.locationtrackerkotlin.utils.StateEnum.DEFAULT
import com.foxminded.android.locationtrackerkotlin.utils.StateEnum.SIGN_OUT
import com.foxminded.android.locationtrackerkotlin.view.BaseCommonFragment
import com.foxminded.android.trackerviewer.R
import com.foxminded.android.trackerviewer.databinding.FragmentMapsBinding
import com.foxminded.android.trackerviewer.di.config.App
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import java.util.*
import javax.inject.Inject

class MapsFragment : BaseCommonFragment() {

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
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun checkState(map: GoogleMap) {
        lifecycleScope.launchWhenStarted {
            viewModel.mapViewState.collect {
                when (it) {
                    is MapViewState.MarkerViewState -> {
                        map.clear()
                        it.markers.forEach { marker ->
                            map.addMarker(marker)
                        }
                    }
                    is MapViewState.SuccessState -> {
                        when (it.state) {
                            SIGN_OUT.state -> {
                                findNavController().navigate(R.id.action_mapsFragment_to_signInFragment)
                                it.state = DEFAULT.state
                            }
                        }

                    }
                    is MapViewState.ErrorState -> {
                        showToastMessage(it.errorMessage)
                        it.errorMessage = null
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
            R.id.refresh -> {
                viewModel.getDataFromFirestore(null)
                binding.chipMap.isVisible = false
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
