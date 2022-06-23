package com.foxminded.android.trackerapp.utils

private const val COLLECTION_NAME = "LocationTrackerDebug"

class FirestoreCollectionNameDebug : IFirestoreCollectionName {

    override fun collectionName(): String {
        return COLLECTION_NAME
    }
}