package com.siva.virtual.util;
import androidx.room.TypeConverter;
import java.util.Date;

public class DateConverter {

    @TypeConverter
    public static Date getDateFromTimeStamp(Long value){
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long getTimeFromDate(Date date){
        return date == null ? null : date.getTime();
    }
}
