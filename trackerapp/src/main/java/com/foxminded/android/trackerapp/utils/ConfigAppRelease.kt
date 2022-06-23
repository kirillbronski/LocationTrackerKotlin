package com.foxminded.android.trackerapp.utils

private const val TIME_RELEASE = 600000L
private const val DISTANCE_RELEASE = 60.0F

class ConfigAppRelease : IConfigApp {
    override fun requestTime(): Long {
        return TIME_RELEASE
    }

    override fun requestDistance(): Float {
        return DISTANCE_RELEASE
    }
}