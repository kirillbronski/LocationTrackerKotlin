package com.foxminded.android.trackerapp

import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.foxminded.android.locationtrackerkotlin.CommonActivity
import com.foxminded.android.trackerapp.signin.SignInFragment

class MainActivity : CommonActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            displayFragment {
                replace(R.id.main_fragment_container, SignInFragment.newInstance())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            }
        }
    }
}