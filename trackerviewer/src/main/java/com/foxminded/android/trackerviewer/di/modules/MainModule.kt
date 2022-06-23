package com.foxminded.android.trackerviewer.di.modules

import androidx.lifecycle.ViewModel
import com.foxminded.android.trackerviewer.BuildConfig
import com.foxminded.android.trackerviewer.factory.ViewModelFactory
import com.foxminded.android.trackerviewer.utils.ConfigAppDebug
import com.foxminded.android.trackerviewer.utils.ConfigAppRelease
import com.foxminded.android.trackerviewer.utils.IConfigApp
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
    fun provideConfigApp(): IConfigApp {
        return if (BuildConfig.DEBUG) {
            ConfigAppDebug()
        } else {
            ConfigAppRelease()
        }
    }

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
    fun provideViewModelFactory(viewModelProviders: @JvmSuppressWildcards Map<Class<out ViewModel>, Provider<ViewModel>>): ViewModelFactory {
        return ViewModelFactory(viewModelProviders)
    }

}