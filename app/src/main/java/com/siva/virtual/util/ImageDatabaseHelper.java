package com.siva.virtual.util;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.siva.virtual.data.ImageDao;
import com.siva.virtual.model.Images;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Images.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class ImageDatabaseHelper extends RoomDatabase{
    public abstract ImageDao imageDao();
    private static final String DATABASE_NAME = "images_db";
    private static final int NUMBER_OF_THREADS = 4;
    private static volatile ImageDatabaseHelper INSTANCE;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static ImageDatabaseHelper getDatebase(final Context context){
        if(INSTANCE == null)
            synchronized (ImageDatabaseHelper.class){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),ImageDatabaseHelper.class,
                            DATABASE_NAME).build();
                }
            }
        return INSTANCE;
    }
}
