package com.foxminded.android.trackerapp.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object DateConvert {
    @SuppressLint("SimpleDateFormat")
    fun dateToStringFormat(date: Long): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-M-d HH:mm:ss")
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date
        return simpleDateFormat.format(calendar.time)
    }
}