package com.foxminded.android.trackerapp.di.modules

import android.location.LocationManager
import android.net.ConnectivityManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.foxminded.android.locationtrackerkotlin.phoneauth.IPhoneAuthRepo
import com.foxminded.android.locationtrackerkotlin.phoneauth.PhoneAuthViewModel
import com.foxminded.android.locationtrackerkotlin.signin.ISignInRepo
import com.foxminded.android.locationtrackerkotlin.signin.SignInViewModel
import com.foxminded.android.locationtrackerkotlin.signup.ISignUpRepo
import com.foxminded.android.locationtrackerkotlin.signup.SignUpViewModel
import com.foxminded.android.trackerapp.di.config.ViewModelKey
import com.foxminded.android.trackerapp.factory.ViewModelFactory
import com.foxminded.android.trackerapp.maps.IMapsRepo
import com.foxminded.android.trackerapp.maps.IMapsRepoFirestore
import com.foxminded.android.trackerapp.maps.MapsViewModel
import com.foxminded.android.trackerapp.utils.IConfigApp
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module(includes = [MainModule::class, RepoModule::class])
class ViewModelModule {

    @IntoMap
    @ViewModelKey(MapsViewModel::class)
    @Provides
    fun providesMapsViewModel(
        mapsRepoImpl: IMapsRepo,
        mapsRepoFirestoreImpl: IMapsRepoFirestore,
        locationManager: LocationManager,
        connectivityManager: ConnectivityManager,
        periodicWorkRequest: PeriodicWorkRequest,
        workManager: WorkManager,
        configApp: IConfigApp,
    ): ViewModel {
        return MapsViewModel(mapsRepoImpl,
            mapsRepoFirestoreImpl,
            locationManager,
            connectivityManager,
            periodicWorkRequest,
            workManager,
            configApp)
    }

    @Provides
    @Singleton
    fun provideInitMapsViewModel(
        viewModelFactory: ViewModelFactory,
    ): MapsViewModel {
        return ViewModelProvider(ViewModelStore(), viewModelFactory)[MapsViewModel::class.java]
    }

    @IntoMap
    @ViewModelKey(SignInViewModel::class)
    @Provides
    fun providesSignInViewModel(signInRepo: ISignInRepo): ViewModel {
        return SignInViewModel(signInRepo)
    }

    @Provides
    @Singleton
    fun provideInitSignInViewModel(
        viewModelFactory: ViewModelFactory,
    ): SignInViewModel {
        return ViewModelProvider(ViewModelStore(), viewModelFactory)[SignInViewModel::class.java]
    }

    @IntoMap
    @ViewModelKey(SignUpViewModel::class)
    @Provides
    fun providesSignUpViewModel(signUpRepo: ISignUpRepo): ViewModel {
        return SignUpViewModel(signUpRepo)
    }

    @Provides
    @Singleton
    fun provideInitSignUpViewModel(
        viewModelFactory: ViewModelFactory,
    ): SignUpViewModel {
        return ViewModelProvider(ViewModelStore(), viewModelFactory)[SignUpViewModel::class.java]
    }

    @IntoMap
    @ViewModelKey(PhoneAuthViewModel::class)
    @Provides
    fun providesPhoneAuthViewModel(phoneAuthRepo: IPhoneAuthRepo): ViewModel {
        return PhoneAuthViewModel(phoneAuthRepo)
    }

    @Provides
    @Singleton
    fun provideInitPhoneAuthViewModel(
        viewModelFactory: ViewModelFactory,
    ): PhoneAuthViewModel {
        return ViewModelProvider(ViewModelStore(), viewModelFactory)[PhoneAuthViewModel::class.java]
    }

}