package com.foxminded.android.trackerviewer.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.foxminded.android.trackerviewer.di.config.ViewModelKey
import com.foxminded.android.trackerviewer.factory.MapsViewModelFactory
import com.foxminded.android.trackerviewer.maps.IMapsRepo
import com.foxminded.android.trackerviewer.maps.MapsFragment
import com.foxminded.android.trackerviewer.maps.MapsViewModel
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module(includes = [MainModule::class, RepoModule::class])
class ViewModelModule {

    lateinit var viewModel: MapsViewModel

    @IntoMap
    @ViewModelKey(MapsViewModel::class)
    @Provides
    fun providesMapsViewModel(mapsRepoImpl: IMapsRepo): ViewModel {
        return MapsViewModel(mapsRepoImpl)
    }

    @Provides
    @Singleton
    fun provideInitMapsViewModel(
        mapsViewModelFactory: MapsViewModelFactory
    ): MapsViewModel {
        return ViewModelProvider(ViewModelStore(), mapsViewModelFactory)[MapsViewModel::class.java]
    }


//    @Provides
//    @Singleton
//    fun provideSignInPresenter(
//        signInRepo: ISignInRepo?,
//        compositeDisposable: CompositeDisposable?
//    ): SignInContract.Presenter? {
//        return CommonPresenterSignIn(signInRepo, compositeDisposable)
//    }
//
//    @Provides
//    @Singleton
//    fun provideSignUpPresenter(
//        signUpRepo: ISignUpRepo?,
//        compositeDisposable: CompositeDisposable?
//    ): SignUpContract.Presenter? {
//        return CommonPresenterSignUp(signUpRepo, compositeDisposable)
//    }
//
//    @Provides
//    @Singleton
//    fun providePhoneAuthPresenter(
//        phoneAuthRepo: IPhoneAuthRepo?,
//        compositeDisposable: CompositeDisposable?
//    ): PhoneAuthContract.Presenter? {
//        return CommonPresenterPhone(phoneAuthRepo, compositeDisposable)
//    }
//
//    @Provides
//    @Singleton
//    fun provideMapsPresenter(
//        compositeDisposable: CompositeDisposable?,
//        mapsRepo: IMapsRepo?,
//        maps: IMaps?
//    ): MapsContract.Presenter? {
//        return MapsPresenter(compositeDisposable, mapsRepo, maps)
//    }

}