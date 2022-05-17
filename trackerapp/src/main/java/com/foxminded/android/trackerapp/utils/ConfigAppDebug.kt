package com.foxminded.android.trackerapp.utils

const val TIME_DEBUG = 5000L
const val DISTANCE_DEBUG = 10.0F

class ConfigAppDebug : IConfigApp {
    override fun requestTime(): Long {
        return TIME_DEBUG
    }

    override fun requestDistance(): Float {
        return DISTANCE_DEBUG
    }
}