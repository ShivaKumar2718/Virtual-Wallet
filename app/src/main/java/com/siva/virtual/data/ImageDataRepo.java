package com.siva.virtual.data;
import android.app.Application;
import androidx.lifecycle.LiveData;
import com.siva.virtual.model.Images;
import com.siva.virtual.util.ImageDatabaseHelper;
import java.util.Date;
import java.util.List;

public class ImageDataRepo {
    private final ImageDao imageDao;
    private final LiveData<List<Images>> allImages;

    public ImageDataRepo(Application application) {
        ImageDatabaseHelper db = ImageDatabaseHelper.getDatebase(application);
        imageDao = db.imageDao();
        allImages = imageDao.getAllImages();
    }

    public LiveData<List<Images>> getAllImages(){
        return allImages;
    }

    public int getCount(String file_name){
        return imageDao.count(file_name);
    }

    public void deleteById(int id){
        ImageDatabaseHelper.databaseWriteExecutor.execute(() -> imageDao.deleteById(id));
    }

    public void updateCreatedDate(Date createdDate,int id){
        ImageDatabaseHelper.databaseWriteExecutor.execute(() -> imageDao.updateCreatedTime(createdDate,id));
    }

    public void insertImage(Images image){
        ImageDatabaseHelper.databaseWriteExecutor.execute(() -> imageDao.insert(image));
    }
}
