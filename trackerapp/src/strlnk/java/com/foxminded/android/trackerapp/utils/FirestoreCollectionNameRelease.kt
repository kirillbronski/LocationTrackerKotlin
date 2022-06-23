package com.foxminded.android.trackerapp.utils

private const val COLLECTION_NAME = "LocationTrackerReleaseStrlnk"

class FirestoreCollectionNameRelease : IFirestoreCollectionName {

    override fun collectionName(): String {
        return COLLECTION_NAME
    }
}