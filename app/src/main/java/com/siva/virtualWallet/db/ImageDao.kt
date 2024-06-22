package com.siva.virtualWallet.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.util.*

@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(images: Images)

    @Query("UPDATE image_table SET created_date=:date WHERE file_id = :id")
    fun updateCreatedTime(date: Date, id: Int)

    @Query("DELETE FROM image_table WHERE file_id = :id")
    fun deleteById(id: Int)

    @get:Query("SELECT * FROM image_table ORDER BY created_date DESC")
    val allImages: LiveData<List<Images>>

    @Query("SELECT COUNT() FROM image_table WHERE file_name = :file_name")
    fun count(file_name: String): Int
}