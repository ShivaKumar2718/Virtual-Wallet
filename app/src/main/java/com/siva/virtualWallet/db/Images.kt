package com.siva.virtualWallet.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "image_table")
data class Images (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("file_id")
    var id : Int,
    var file_type: String?,
    var front_image: String?,
    var back_image: String?,
    var file_name: String?,
    var back_file_name: String?,
    var created_date: Date?
)
