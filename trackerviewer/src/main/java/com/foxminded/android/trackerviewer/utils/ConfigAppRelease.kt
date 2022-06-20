package com.foxminded.android.trackerviewer.utils

private const val COLLECTION_NAME_RELEASE = "LocationTrackerRelease"

class ConfigAppRelease : IConfigApp {

    override fun firestoreCollectionName(): String {
        return COLLECTION_NAME_RELEASE
    }
}