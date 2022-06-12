package com.foxminded.android.trackerapp.di.config

import android.app.Application
import com.foxminded.android.trackerapp.di.component.DaggerMainComponent
import com.foxminded.android.trackerapp.di.component.MainComponent
import com.foxminded.android.trackerapp.di.modules.MainModule
import com.foxminded.android.trackerapp.di.modules.RepoModule
import com.foxminded.android.trackerapp.di.modules.ViewModelModule

class App() : Application() {

    lateinit var mainComponent: MainComponent

    override fun onCreate() {
        super.onCreate()
        mainComponent = buildMainComponent()
    }

    private fun buildMainComponent(): MainComponent {
        return DaggerMainComponent.builder()
            .mainModule(MainModule(this))
            .repoModule(RepoModule())
            .viewModelModule(ViewModelModule())
            .build()
    }
}