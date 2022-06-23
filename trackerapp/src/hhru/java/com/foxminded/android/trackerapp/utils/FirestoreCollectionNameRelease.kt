package com.foxminded.android.trackerapp.utils

private const val COLLECTION_NAME = "LocationTrackerReleaseHhru"

class FirestoreCollectionNameRelease : IFirestoreCollectionName {

    override fun collectionName(): String {
        return COLLECTION_NAME
    }
}