package com.foxminded.android.trackerviewer.di.component

import com.foxminded.android.trackerviewer.di.modules.MainModule
import com.foxminded.android.trackerviewer.di.modules.RepoModule
import com.foxminded.android.trackerviewer.di.modules.ViewModelModule
import com.foxminded.android.trackerviewer.maps.MapsFragment
import com.foxminded.android.trackerviewer.phoneauth.PhoneAuthFragment
import com.foxminded.android.trackerviewer.signin.SignInFragment
import com.foxminded.android.trackerviewer.signup.SignUpFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [MainModule::class, ViewModelModule::class, RepoModule::class])
interface MainComponent {

    fun injectMapsFragment(view: MapsFragment)

    fun injectPhoneAuthFragment(view: PhoneAuthFragment)

    fun injectSignInFragment(view: SignInFragment)

    fun injectSignUpFragment(view: SignUpFragment)
}