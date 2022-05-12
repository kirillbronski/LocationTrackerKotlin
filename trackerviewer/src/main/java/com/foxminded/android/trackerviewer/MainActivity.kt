package com.foxminded.android.trackerviewer

import android.os.Bundle
import com.foxminded.android.locationtrackerkotlin.MainActivityCommon
import com.foxminded.android.trackerviewer.signin.SignInFragment

class MainActivity : MainActivityCommon() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            displayFragment(SignInFragment.newInstance())
        }
    }
}