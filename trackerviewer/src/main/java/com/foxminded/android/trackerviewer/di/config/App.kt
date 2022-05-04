package com.foxminded.android.trackerviewer.di.config

import android.app.Application
import com.foxminded.android.trackerviewer.di.component.DaggerMainComponent
import com.foxminded.android.trackerviewer.di.component.MainComponent
import com.foxminded.android.trackerviewer.di.modules.MainModule
import com.foxminded.android.trackerviewer.di.modules.RepoModule
import com.foxminded.android.trackerviewer.di.modules.ViewModelModule

class App() : Application() {

    lateinit var mainComponent: MainComponent

    override fun onCreate() {
        super.onCreate()
        mainComponent = DaggerMainComponent.create()
    }
}