package com.foxminded.android.trackerviewer.di.modules

import com.foxminded.android.trackerviewer.maps.IMapsRepo
import com.foxminded.android.trackerviewer.maps.MapsRepoImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [MainModule::class])
class RepoModule {

//    @Provides
//    @Singleton
//    fun signUpRepo(auth: FirebaseAuth?): ISignUpRepo? {
//        return SignUpRepoImpl(auth)
//    }
//
//    @Provides
//    @Singleton
//    fun signInRepo(auth: FirebaseAuth?): ISignInRepo? {
//        return SignInRepoImpl(auth)
//    }
//
//    @Provides
//    @Singleton
//    fun phoneAuthRepo(auth: FirebaseAuth?): IPhoneAuthRepo? {
//        return PhoneAuthRepoImpl(auth)
//    }

    @Provides
    @Singleton
    fun provideMapsRepo(firebaseFirestore: FirebaseFirestore, firebaseAuth: FirebaseAuth): IMapsRepo {
        return MapsRepoImpl(firebaseFirestore, firebaseAuth)
    }

}