package com.foxminded.android.trackerapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.foxminded.android.trackerapp.services.TrackerService
import com.foxminded.android.trackerapp.utils.Constants.ACTION_SHOW_MAP_FRAGMENT
import com.foxminded.android.trackerapp.utils.Constants.ACTION_STOP_SERVICE

class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_main)
        navigateToMapFragmentIfNeeded(intent)
        settingsActionBar()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToMapFragmentIfNeeded(intent)
    }

    private fun settingsActionBar() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.main_fragment_container) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun navigateToMapFragmentIfNeeded(intent: Intent?) {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.main_fragment_container) as NavHostFragment
        if (intent?.action == ACTION_SHOW_MAP_FRAGMENT) {
            navHostFragment.findNavController().navigate(R.id.action_global_map_fragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Intent(this, TrackerService::class.java).also {
            it.action = ACTION_STOP_SERVICE
            stopService(it)
        }
    }
}