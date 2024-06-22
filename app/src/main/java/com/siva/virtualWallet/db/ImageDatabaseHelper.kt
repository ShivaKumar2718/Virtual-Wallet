package com.siva.virtualWallet.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.siva.virtualWallet.util.DateConverter

@Database(entities = [Images::class], version = 1, exportSchema = false)
@TypeConverters(
    DateConverter::class
)
abstract class ImageDatabaseHelper : RoomDatabase() {
    abstract fun imageDao(): ImageDao

    companion object{
        private var INSTANCE : ImageDatabaseHelper? = null
        fun getDatabaseInstance(context: Context): ImageDatabaseHelper {
            synchronized(this){
                var instance = INSTANCE
                if (instance == null){
                    instance = databaseBuilder(context.applicationContext,
                        ImageDatabaseHelper::class.java,
                        "images_db").build()
                }
                return instance
            }
        }
    }

//    companion object {
//        private const val DATABASE_NAME =
//            "images_db"
//        private const val NUMBER_OF_THREADS =
//            4
//
//        @Volatile
//        private var INSTANCE: ImageDatabaseHelper? =
//            null
//        val databaseWriteExecutor: ExecutorService =
//            Executors.newFixedThreadPool(NUMBER_OF_THREADS)
//
//        fun getDatebase(context: Context): ImageDatabaseHelper? {
//            if (INSTANCE == null) synchronized(ImageDatabaseHelper::class.java) {
//                if (INSTANCE == null) {
//                    INSTANCE =
//                        databaseBuilder(
//                            context.applicationContext, ImageDatabaseHelper::class.java,
//                            DATABASE_NAME
//                        ).build()
//                }
//            }
//            return INSTANCE
//        }
//    }
}