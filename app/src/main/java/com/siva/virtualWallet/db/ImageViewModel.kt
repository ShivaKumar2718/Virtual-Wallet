package com.siva.virtualWallet.db
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class ImageViewModel(private val imageDao: ImageDao) : ViewModel() {
    val allImages = imageDao.allImages

    fun getCount(file_name: String): Int {
        return imageDao.count(file_name)
    }

    fun deleteById(id: Int)=viewModelScope.launch(Dispatchers.IO) {
        imageDao.deleteById(id)
    }

    fun updateCreatedDate(createdDate: Date, id: Int)=viewModelScope.launch(Dispatchers.IO) {
        imageDao.updateCreatedTime(
            createdDate,
            id
        )
    }

    fun insertImage(image: Images?)=viewModelScope.launch(Dispatchers.IO) {
        if (image != null) {
            imageDao.insert(image)
        }
    }
}