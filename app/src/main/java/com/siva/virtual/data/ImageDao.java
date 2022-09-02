package com.siva.virtual.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.siva.virtual.model.Images;

import java.util.Date;
import java.util.List;

@Dao
public interface ImageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Images images);

    @Query("UPDATE image_table SET created_date=:date WHERE file_id = :id")
    void updateCreatedTime(Date date, int id);

    @Query("DELETE FROM image_table WHERE file_id = :id")
    void deleteById(int id);

    @Query("SELECT * FROM image_table ORDER BY created_date DESC")
    LiveData<List<Images>> getAllImages();

    @Query("SELECT COUNT() FROM image_table WHERE file_name = :file_name")
    int count(String file_name);

}
