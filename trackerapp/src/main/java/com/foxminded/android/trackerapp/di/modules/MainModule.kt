package com.foxminded.android.trackerapp.di.modules

import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import androidx.lifecycle.ViewModel
import androidx.room.Room
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.foxminded.android.locationtrackerkotlin.BuildConfig
import com.foxminded.android.trackerapp.factory.ViewModelFactory
import com.foxminded.android.trackerapp.room.AccountDao
import com.foxminded.android.trackerapp.room.LocationDatabase
import com.foxminded.android.trackerapp.utils.ConfigAppDebug
import com.foxminded.android.trackerapp.utils.ConfigAppRelease
import com.foxminded.android.trackerapp.utils.IConfigApp
import com.foxminded.android.trackerapp.utils.SendData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import java.util.concurrent.TimeUnit
import javax.inject.Provider
import javax.inject.Singleton

@Module
class MainModule(
    private val context: Context,
) {

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
    fun provideConstraints(): Constraints {
        return Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
    }

    @Provides
    @Singleton
    fun providePeriodicWorkRequest(): PeriodicWorkRequest {
        return PeriodicWorkRequest.Builder(SendData::class.java,
            15, TimeUnit.MINUTES)
            .setConstraints(provideConstraints())
            .build()
    }

    @Provides
    @Singleton
    fun provideWorkManager(): WorkManager {
        return WorkManager.getInstance(context)
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

    @Provides
    @Singleton
    fun provideDatabase(): LocationDatabase {
        return Room.databaseBuilder(context, LocationDatabase::class.java, "database").build()
    }

    @Provides
    fun provideAccountDao(): AccountDao {
        return provideDatabase().accountDao()
    }

    @Provides
    @Singleton
    fun provideLocationManager(): LocationManager {
        return context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    }

    @Provides
    @Singleton
    fun provideConnectivityManager(): ConnectivityManager {
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

}