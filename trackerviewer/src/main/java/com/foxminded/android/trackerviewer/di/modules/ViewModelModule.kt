package com.foxminded.android.trackerviewer.di.modules

import androidx.lifecycle.ViewModel
import com.foxminded.android.trackerviewer.di.config.ViewModelKey
import com.foxminded.android.trackerviewer.maps.IMapsRepo
import com.foxminded.android.trackerviewer.maps.MapsViewModel
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module(includes = [MainModule::class, RepoModule::class])
class ViewModelModule {

    @IntoMap
    @ViewModelKey(MapsViewModel::class)
    @Provides
    fun providesMapsViewModel(mapsRepoImpl: IMapsRepo): ViewModel {
        return MapsViewModel(mapsRepoImpl)
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