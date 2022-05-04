package com.foxminded.android.trackerviewer.di.component

import com.foxminded.android.trackerviewer.di.modules.MainModule
import com.foxminded.android.trackerviewer.di.modules.RepoModule
import com.foxminded.android.trackerviewer.di.modules.ViewModelModule
import com.foxminded.android.trackerviewer.maps.MapsFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [MainModule::class, ViewModelModule::class, RepoModule::class])
interface MainComponent {

    fun injectMapsFragment(view: MapsFragment)
}