package com.foxminded.android.locationtrackerkotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentTransaction
import com.foxminded.android.locationtrackerkotlin.extensions.inTransaction

abstract class CommonActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_main)
    }

    fun displayFragment(
        transaction: FragmentTransaction.() -> FragmentTransaction,
    ) {
        supportFragmentManager.inTransaction {
            transaction.invoke(this)
        }
    }
}