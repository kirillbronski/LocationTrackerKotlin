package com.foxminded.android.trackerapp.di.component

import com.foxminded.android.trackerapp.di.modules.MainModule
import com.foxminded.android.trackerapp.di.modules.RepoModule
import com.foxminded.android.trackerapp.di.modules.ViewModelModule
import com.foxminded.android.trackerapp.maps.MapsFragment
import com.foxminded.android.trackerapp.phoneauth.PhoneAuthFragment
import com.foxminded.android.trackerapp.signin.SignInFragment
import com.foxminded.android.trackerapp.signup.SignUpFragment
import com.foxminded.android.trackerapp.utils.SendData
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [MainModule::class, ViewModelModule::class, RepoModule::class])
interface MainComponent {

    fun injectMapsFragment(view: MapsFragment)

    fun injectPhoneAuthFragment(view: PhoneAuthFragment)

    fun injectSignInFragment(view: SignInFragment)

    fun injectSignUpFragment(view: SignUpFragment)

    fun injectSendData(view: SendData)
}