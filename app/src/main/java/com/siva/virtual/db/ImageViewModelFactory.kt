package com.siva.virtual.db

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ImageViewModelFactory(private val imageDao: ImageDao): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ImageViewModel::class.java))
            return ImageViewModel(imageDao) as T
        throw java.lang.IllegalArgumentException("Unknown ViewModel Class")
    }
}