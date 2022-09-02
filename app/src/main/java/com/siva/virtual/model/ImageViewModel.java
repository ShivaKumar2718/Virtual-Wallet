package com.siva.virtual.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.siva.virtual.data.ImageDataRepo;
import com.siva.virtual.util.ImageDatabaseHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ImageViewModel extends AndroidViewModel {
    public static ImageDataRepo imageDataRepo;
    public final LiveData<List<Images>> allImages;
    public final List<Images> images = new ArrayList<>();

    public ImageViewModel(@NonNull Application application) {
        super(application);
        imageDataRepo = new ImageDataRepo(application);
        allImages = imageDataRepo.getAllImages();
    }

    public LiveData<List<Images>> getAllImages(){
        return this.allImages;
    }

    public int getCount(String file_name){
        return imageDataRepo.getCount(file_name);
    }

    public void deleteById(int id){
        ImageDatabaseHelper.databaseWriteExecutor.execute(() -> imageDataRepo.deleteById(id));
    }
    public void updateCreatedDate(Date createdDate, int id){
        ImageDatabaseHelper.databaseWriteExecutor.execute(() -> imageDataRepo.updateCreatedDate(createdDate,id));
    }

    public static void insertImage(Images image){
        imageDataRepo.insertImage(image);
    }
}
