package com.foxminded.android.trackerapp.utils

interface IConfigApp {
    fun requestTime(): Long
    fun requestDistance(): Float
    fun firestoreCollectionName(): String
}