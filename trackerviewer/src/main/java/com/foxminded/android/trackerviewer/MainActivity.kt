package com.foxminded.android.trackerviewer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE
import com.foxminded.android.trackerviewer.di.config.App
import com.foxminded.android.trackerviewer.maps.MapsFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            displayFragment(MapsFragment.newInstance())
        }
    }

    private fun displayFragment(fragment: MapsFragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, fragment)
            .setTransition(TRANSIT_FRAGMENT_FADE)
            .addToBackStack(null)
            .commit()
    }
}