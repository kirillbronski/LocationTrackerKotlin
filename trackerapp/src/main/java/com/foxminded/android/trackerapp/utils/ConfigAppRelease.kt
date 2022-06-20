package com.foxminded.android.trackerapp.utils

private const val TIME_RELEASE = 600000L
private const val DISTANCE_RELEASE = 60.0F
private const val COLLECTION_NAME_RELEASE = "LocationTrackerRelease"

class ConfigAppRelease : IConfigApp {
    override fun requestTime(): Long {
        return TIME_RELEASE
    }

    override fun requestDistance(): Float {
        return DISTANCE_RELEASE
    }

    override fun firestoreCollectionName(): String {
        return COLLECTION_NAME_RELEASE
    }
}