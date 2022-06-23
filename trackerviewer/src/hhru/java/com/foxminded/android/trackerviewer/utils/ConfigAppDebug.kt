package com.foxminded.android.trackerviewer.utils

private const val COLLECTION_NAME = "LocationTrackerDebugHhru"

class ConfigAppDebug : IConfigApp {

    override fun firestoreCollectionName(): String {
        return COLLECTION_NAME
    }
}