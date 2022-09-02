package com.siva.virtual.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "image_table")
public class Images {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "file_id")
    private int id;

    private String file_type;

    private String front_image;

    private String back_image;

    private String file_name;

    private String back_file_name;

    private Date created_date;

    public Images() {
    }

    public String getBack_file_name() {
        return back_file_name;
    }

    public void setBack_file_name(String back_file_name) {
        this.back_file_name = back_file_name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getFile_type() {
        return file_type;
    }

    public String getFront_image() {
        return front_image;
    }

    public void setFront_image(String front_image) {
        this.front_image = front_image;
    }

    public String getBack_image() {
        return back_image;
    }

    public void setBack_image(String back_image) {
        this.back_image = back_image;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public Date getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Date created_date) {
        this.created_date = created_date;
    }
}
