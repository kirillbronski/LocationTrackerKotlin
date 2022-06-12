package com.foxminded.android.trackerapp.di.modules

import android.location.LocationManager
import android.net.ConnectivityManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.foxminded.android.locationtrackerkotlin.accountinfo.AccountInfoViewModel
import com.foxminded.android.locationtrackerkotlin.accountinfo.IAccountInfoRepo
import com.foxminded.android.locationtrackerkotlin.forgotpassword.ForgotPasswordViewModel
import com.foxminded.android.locationtrackerkotlin.forgotpassword.IForgotPasswordRepo
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
    fun provideMapsViewModel(
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
    fun provideSignInViewModel(signInRepo: ISignInRepo): ViewModel {
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
    fun provideSignUpViewModel(signUpRepo: ISignUpRepo): ViewModel {
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
    fun providePhoneAuthViewModel(phoneAuthRepo: IPhoneAuthRepo): ViewModel {
        return PhoneAuthViewModel(phoneAuthRepo)
    }

    @Provides
    @Singleton
    fun provideInitPhoneAuthViewModel(
        viewModelFactory: ViewModelFactory,
    ): PhoneAuthViewModel {
        return ViewModelProvider(ViewModelStore(), viewModelFactory)[PhoneAuthViewModel::class.java]
    }

    @IntoMap
    @ViewModelKey(AccountInfoViewModel::class)
    @Provides
    fun provideAccountInfoViewModel(accountInfoRepoImpl: IAccountInfoRepo): ViewModel {
        return AccountInfoViewModel(accountInfoRepoImpl)
    }

    @Provides
    @Singleton
    fun provideInitAccountInfoViewModel(
        viewModelFactory: ViewModelFactory,
    ): AccountInfoViewModel {
        return ViewModelProvider(ViewModelStore(),
            viewModelFactory)[AccountInfoViewModel::class.java]
    }

    @IntoMap
    @ViewModelKey(ForgotPasswordViewModel::class)
    @Provides
    fun provideForgotPasswordViewModel(forgotPasswordRepoImpl: IForgotPasswordRepo): ViewModel {
        return ForgotPasswordViewModel(forgotPasswordRepoImpl)
    }

    @Provides
    @Singleton
    fun provideInitForgotPasswordViewModel(
        viewModelFactory: ViewModelFactory,
    ): ForgotPasswordViewModel {
        return ViewModelProvider(ViewModelStore(),
            viewModelFactory)[ForgotPasswordViewModel::class.java]
    }
}