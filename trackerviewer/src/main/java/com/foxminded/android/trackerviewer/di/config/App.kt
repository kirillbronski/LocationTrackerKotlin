package com.foxminded.android.trackerviewer.di.config

import android.app.Application
import com.foxminded.android.trackerviewer.di.component.DaggerMainComponent
import com.foxminded.android.trackerviewer.di.component.MainComponent

class App() : Application() {

    lateinit var mainComponent: MainComponent

    override fun onCreate() {
        super.onCreate()
        mainComponent = DaggerMainComponent.create()
    }
}