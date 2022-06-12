package com.foxminded.android.trackerviewer.di.modules

import com.foxminded.android.locationtrackerkotlin.accountinfo.AccountInfoRepoImpl
import com.foxminded.android.locationtrackerkotlin.accountinfo.IAccountInfoRepo
import com.foxminded.android.locationtrackerkotlin.forgotpassword.ForgotPasswordRepoImpl
import com.foxminded.android.locationtrackerkotlin.forgotpassword.IForgotPasswordRepo
import com.foxminded.android.locationtrackerkotlin.phoneauth.IPhoneAuthRepo
import com.foxminded.android.locationtrackerkotlin.phoneauth.PhoneAuthRepoImpl
import com.foxminded.android.locationtrackerkotlin.signin.ISignInRepo
import com.foxminded.android.locationtrackerkotlin.signin.SignInRepoImpl
import com.foxminded.android.locationtrackerkotlin.signup.ISignUpRepo
import com.foxminded.android.locationtrackerkotlin.signup.SignUpRepoImpl
import com.foxminded.android.trackerviewer.maps.IMapsRepo
import com.foxminded.android.trackerviewer.maps.MapsRepoImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [MainModule::class])
class RepoModule {

    @Provides
    @Singleton
    fun signUpRepo(firebaseAuth: FirebaseAuth): ISignUpRepo {
        return SignUpRepoImpl(firebaseAuth)
    }

    @Provides
    @Singleton
    fun signInRepo(firebaseAuth: FirebaseAuth): ISignInRepo {
        return SignInRepoImpl(firebaseAuth)
    }

    @Provides
    @Singleton
    fun phoneAuthRepo(firebaseAuth: FirebaseAuth): IPhoneAuthRepo {
        return PhoneAuthRepoImpl(firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideMapsRepo(
        firebaseFirestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth,
    ): IMapsRepo {
        return MapsRepoImpl(firebaseFirestore, firebaseAuth)
    }

    @Provides
    @Singleton
    fun phoneAccountInfoRepo(firebaseAuth: FirebaseAuth): IAccountInfoRepo {
        return AccountInfoRepoImpl(firebaseAuth)
    }

    @Provides
    @Singleton
    fun phoneForgotPasswordRepo(firebaseAuth: FirebaseAuth): IForgotPasswordRepo {
        return ForgotPasswordRepoImpl(firebaseAuth)
    }
}