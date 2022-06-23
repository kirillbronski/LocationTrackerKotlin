package com.foxminded.android.trackerapp.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat.IMPORTANCE_LOW
import androidx.lifecycle.LifecycleService
import com.foxminded.android.trackerapp.MainActivity
import com.foxminded.android.trackerapp.R
import com.foxminded.android.trackerapp.utils.Constants.ACTION_SHOW_MAP_FRAGMENT
import com.foxminded.android.trackerapp.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import com.foxminded.android.trackerapp.utils.Constants.ACTION_STOP_SERVICE
import com.foxminded.android.trackerapp.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.foxminded.android.trackerapp.utils.Constants.NOTIFICATION_CHANNEL_NAME
import com.foxminded.android.trackerapp.utils.Constants.NOTIFICATION_ID

class TrackerService : LifecycleService() {

    private val TAG = TrackerService::class.java.simpleName

    private var isFirstRun = true

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstRun) {
                        startForegroundService()
                        isFirstRun = false
                        Log.d(TAG, "onStartCommand: ACTION_START_OR_RESUME_SERVICE")
                    } else {
                        Log.d(TAG, "onStartCommand: resuming service")
                    }
                }
                ACTION_STOP_SERVICE -> {
                    stopForeground(NOTIFICATION_ID)
                    Log.d(TAG, "onStartCommand: ACTION_STOP_SERVICE")
                }
                else -> {
                    Log.d(TAG, "onStartCommand: ELSE")
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }
        val notificationBuilder = NotificationCompat
            .Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_baseline_add_location_alt_24)
            .setContentTitle("Location Tracker")
            .setContentTitle("App is running")
            .setContentIntent(getMainActivityPendingIntent())

        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun getMainActivityPendingIntent() = getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also {
            it.action = ACTION_SHOW_MAP_FRAGMENT
        },
        FLAG_IMMUTABLE
    )

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }
}






















