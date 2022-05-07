package com.foxminded.android.trackerviewer.di.modules

import androidx.lifecycle.ViewModel
import com.foxminded.android.trackerviewer.factory.MapsViewModelFactory
import com.foxminded.android.trackerviewer.maps.MapsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import javax.inject.Provider
import javax.inject.Singleton

@Module
class MainModule() {

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideMapsViewModelFactory(viewModelProviders: @JvmSuppressWildcards Map<Class<out ViewModel>, Provider<ViewModel>>): MapsViewModelFactory {
        return MapsViewModelFactory(viewModelProviders)
    }

}