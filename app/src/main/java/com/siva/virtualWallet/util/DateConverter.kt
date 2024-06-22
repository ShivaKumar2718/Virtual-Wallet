package com.siva.virtualWallet.util

import androidx.room.TypeConverter
import java.util.*

object DateConverter {
    @JvmStatic
    @TypeConverter
    fun getDateFromTimeStamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @JvmStatic
    @TypeConverter
    fun getTimeFromDate(date: Date?): Long? {
        return date?.time
    }
}