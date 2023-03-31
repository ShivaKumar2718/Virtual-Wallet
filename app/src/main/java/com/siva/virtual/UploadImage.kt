package com.siva.virtual

import android.Manifest
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.ContactsContract.CommonDataKinds.Im
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.siva.virtual.databinding.ActivityUploadImageBinding
import com.siva.virtual.db.ImageDatabaseHelper
import com.siva.virtual.db.ImageViewModel
import com.siva.virtual.db.ImageViewModelFactory
import com.siva.virtual.db.Images
import com.siva.virtual.util.ImageFilePath
import com.siva.virtual.util.Utils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class UploadImage : AppCompatActivity() {
    private lateinit var binding: ActivityUploadImageBinding
    var front_bitmap: Bitmap? =
        null
    var back_bitmap: Bitmap? =
        null
    private var from_upload =
        false
    private var from_front_side =
        false
    private var card_type: String? =
        null
    private var file_name =
        ""
    private var file_path: String? =
        null
    private lateinit var imageViewModel: ImageViewModel

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageDao = ImageDatabaseHelper.getDatabaseInstance(applicationContext).imageDao()
        val factory = ImageViewModelFactory(imageDao)
        imageViewModel = ViewModelProvider(this,factory)[ImageViewModel::class.java]

        // Create an ArrayAdapter using the string array and a default spinner
        val adapter =
            ArrayAdapter
                .createFromResource(
                    this, R.array.file_types,
                    android.R.layout.simple_spinner_item
                )

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        binding.spinner.adapter =
            adapter
        binding.spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>,
                    view: View,
                    i: Int,
                    l: Long
                ) {
                    card_type =
                        adapterView.getItemAtPosition(i).toString()
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }
        binding.btnSave.setOnClickListener { view: View? ->
            //save the file here
            if (card_type != resources.getStringArray(R.array.file_types)[0]) {
                if (front_bitmap == null && back_bitmap == null) {
                    Utils.showErrorLay(
                        this@UploadImage,
                        binding.uploadErrorLay.cardTop,
                        binding.uploadErrorLay.errorLay,
                        binding.uploadErrorLay.tvInfo,
                        "Atleast one side of the card is required!"
                    )
                } else {
                    if (Objects.requireNonNull(binding.etFileName.text).toString()
                            .trim { it <= ' ' }
                            .isEmpty()
                    ) {
                        Utils.showErrorLay(
                            this@UploadImage,
                            binding.uploadErrorLay.cardTop,
                            binding.uploadErrorLay.errorLay,
                            binding.uploadErrorLay.tvInfo,
                            "Filename please!"
                        )
                    } else {
                        file_name =
                            binding.etFileName.text.toString().trim { it <= ' ' }
                        AsyncTask.execute {
                            if (imageViewModel.getCount(file_name) > 0) Snackbar.make(
                                this@UploadImage,
                                binding.btnSave,
                                "Filename Already exists!",
                                Snackbar.LENGTH_SHORT
                            ).show() else {
                                storeImagesIntoDB()
                            }
                        }
                    }
                }
            } else {
                Utils.showErrorLay(
                    this@UploadImage,
                    binding.uploadErrorLay.cardTop,
                    binding.uploadErrorLay.errorLay,
                    binding.uploadErrorLay.tvInfo,
                    "Please select file type!"
                )
            }
        }
        binding.uploadErrorLay.fadeLay.setOnClickListener { view: View? ->
            Utils.hideErrorLay(
                this@UploadImage,
                binding.uploadErrorLay.cardTop,
                binding.uploadErrorLay.errorLay
            )
        }
        binding.uploadLay.setOnClickListener { view: View ->
            Utils.hideKeyBoard(
                this@UploadImage,
                view
            )
        }
        binding.camera.setOnClickListener { view: View? ->
            from_front_side =
                true
            from_upload =
                false
            binding.frontSideImg.visibility =
                View.VISIBLE
            binding.frontView.visibility =
                View.VISIBLE
            checkCameraPermission()
        }
        binding.upload.setOnClickListener { view: View? ->
            from_front_side =
                true
            from_upload =
                true
            binding.frontSideImg.visibility =
                View.VISIBLE
            binding.frontView.visibility =
                View.VISIBLE
            checkGalleryPermission()
        }
        binding.ivClose.setOnClickListener { view: View? ->
            binding.ivClose.visibility =
                View.GONE
            binding.ivRotate.visibility =
                View.GONE
            binding.frontSideImg.setImageBitmap(null)
            front_bitmap =
                null
            binding.frontLay.visibility =
                View.VISIBLE
        }
        binding.ivRotate.setOnClickListener { view: View? ->
            front_bitmap =
                rotateBitmap(front_bitmap)
            binding.frontSideImg.setImageBitmap(front_bitmap)
        }
        binding.cameraBack.setOnClickListener { view: View? ->
            from_front_side =
                false
            from_upload =
                false
            binding.backSideImg.visibility =
                View.VISIBLE
            binding.backView.visibility =
                View.VISIBLE
            checkCameraPermission()
        }
        binding.uploadBack.setOnClickListener { view: View? ->
            from_front_side =
                false
            from_upload =
                true
            binding.backSideImg.visibility =
                View.VISIBLE
            binding.backView.visibility =
                View.VISIBLE
            checkGalleryPermission()
        }
        binding.ivBackClose.setOnClickListener { view: View? ->
            binding.ivBackClose.visibility =
                View.GONE
            binding.ivBackRotate.visibility =
                View.GONE
            binding.backSideImg.setImageBitmap(null)
            back_bitmap =
                null
            binding.backLay.visibility =
                View.VISIBLE
        }
        binding.ivBackRotate.setOnClickListener { view: View? ->
            back_bitmap =
                rotateBitmap(back_bitmap)
            binding.backSideImg.setImageBitmap(back_bitmap)
        }
    }

    private fun rotateBitmap(front_bitmap: Bitmap?): Bitmap {
        val matrix =
            Matrix()
        matrix.postRotate(90f)
        return Bitmap.createBitmap(
            front_bitmap!!,
            0,
            0,
            front_bitmap.width,
            front_bitmap.height,
            matrix,
            true
        )
    }

    private fun storeImagesIntoDB() {
        imageViewModel.insertImage(Images(0,card_type,if (front_bitmap != null) saveToInternalStorage(front_bitmap!!, file_name) else null,
            if (back_bitmap != null) saveToInternalStorage(back_bitmap!!, "back_$file_name") else null,
            file_name,"back_$file_name",Calendar.getInstance().time))
        Utils.moveToAnotherActivity(this@UploadImage, MainActivity::class.java)
    }

    private fun checkGalleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    MY_GALLERY_REQUEST_CODE
                )
            } else {
                selectImage()
            }
        } else selectImage()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permission granted
                captureImage()
            } else {
                if (from_front_side) {
                    binding!!.frontView.visibility =
                        View.GONE
                    binding!!.frontSideImg.visibility =
                        View.GONE
                } else {
                    binding!!.backView.visibility =
                        View.GONE
                    binding!!.backSideImg.visibility =
                        View.GONE
                }
                Utils.showErrorLay(
                    this@UploadImage,
                    binding!!.uploadErrorLay.cardTop,
                    binding!!.uploadErrorLay.errorLay,
                    binding!!.uploadErrorLay.tvInfo,
                    "Permission is needed to access the camera. Please allow it from the app settings"
                )
            }
        }
        if (requestCode == MY_GALLERY_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage()
            } else {
                if (from_front_side) {
                    binding!!.frontView.visibility =
                        View.GONE
                    binding!!.frontSideImg.visibility =
                        View.GONE
                } else {
                    binding!!.backView.visibility =
                        View.GONE
                    binding!!.backSideImg.visibility =
                        View.GONE
                }
                Utils.showErrorLay(
                    this@UploadImage,
                    binding!!.uploadErrorLay.cardTop,
                    binding!!.uploadErrorLay.errorLay,
                    binding!!.uploadErrorLay.tvInfo,
                    "Permission is needed to access the gallery. Please allow it from the app settings"
                )
            }
        }
    }

    private var activityResultLaunch =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                if (!from_upload) {
                    if (from_front_side) {
                        front_bitmap =
                            roteIfRequired(scalePic(binding!!.frontSideImg, file_path))
                        setImageOnScreen(
                            front_bitmap,
                            binding!!.frontSideImg,
                            binding!!.ivClose,
                            binding!!.frontLay,
                            binding!!.ivRotate,
                            binding!!.frontView
                        )
                    } else {
                        back_bitmap =
                            roteIfRequired(scalePic(binding!!.backSideImg, file_path))
                        setImageOnScreen(
                            back_bitmap,
                            binding!!.backSideImg,
                            binding!!.ivBackClose,
                            binding!!.backLay,
                            binding!!.ivBackRotate,
                            binding!!.backView
                        )
                    }
                } else {
                    //coming from upload
                    val imageUri =
                        result.data?.data
                    if (from_front_side) {
                        front_bitmap =
                            scalePic(
                                binding!!.frontSideImg,
                                imageUri?.let { ImageFilePath.getPath(this@UploadImage, it) }
                            )
                        setImageOnScreen(
                            front_bitmap,
                            binding!!.frontSideImg,
                            binding!!.ivClose,
                            binding!!.frontLay,
                            binding!!.ivRotate,
                            binding!!.frontView
                        )
                    } else {
                        back_bitmap =
                            scalePic(
                                binding!!.backSideImg,
                                imageUri?.let { ImageFilePath.getPath(this@UploadImage, it) }
                            )
                        setImageOnScreen(
                            back_bitmap,
                            binding!!.backSideImg,
                            binding!!.ivBackClose,
                            binding!!.backLay,
                            binding!!.ivBackRotate,
                            binding!!.backView
                        )
                    }
                }
            } else {
                if (front_bitmap == null) {
                    binding!!.frontSideImg.visibility =
                        View.GONE
                    binding!!.frontView.visibility =
                        View.GONE
                }
                if (back_bitmap == null) {
                    binding!!.backSideImg.visibility =
                        View.GONE
                    binding!!.backView.visibility =
                        View.GONE
                }
            }
        }

    private fun roteIfRequired(bitmap: Bitmap): Bitmap {
        val result: Bitmap
        val width =
            bitmap.width
        val height =
            bitmap.height
        result =
            if (width > height) rotateBitmap(bitmap) else bitmap
        return result
    }

    private fun scalePic(imageView: AppCompatImageView, file_path: String?): Bitmap {
        // Get the dimensions of the View
        val targetW =
            imageView.width
        val targetH =
            imageView.height

        // Get the dimensions of the bitmap
        val bmOptions =
            BitmapFactory.Options()
        bmOptions.inJustDecodeBounds =
            true
        BitmapFactory.decodeFile(file_path, bmOptions)
        val photoW =
            bmOptions.outWidth
        val photoH =
            bmOptions.outHeight
        var scaleFactor =
            0

        // Determine how much to scale down the image
        try {
            scaleFactor =
                Math.min(photoW / targetW, photoH / targetH)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds =
            false
        bmOptions.inSampleSize =
            scaleFactor
        return BitmapFactory.decodeFile(file_path, bmOptions)
    }

    private fun saveToInternalStorage(bitmapImage: Bitmap, file_name: String): String {
        val cw =
            ContextWrapper(applicationContext)
        // path to /data/data/yourapp/app_data/imageDir
        val directory =
            cw.getDir("imageDir", MODE_PRIVATE)
        // Create imageDir
        val mypath =
            File(directory, file_name)
        var fos: FileOutputStream? =
            null
        try {
            fos =
                FileOutputStream(mypath)
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                assert(fos != null)
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return directory.absolutePath
    }

    fun setImageOnScreen(
        bitmap: Bitmap?,
        iv1: AppCompatImageView,
        iv2: AppCompatImageView,
        lay: LinearLayoutCompat,
        Rotate: AppCompatImageView,
        view: View
    ) {
        iv1.visibility =
            View.VISIBLE
        iv1.setImageBitmap(bitmap)
        iv2.visibility =
            View.VISIBLE
        Rotate.visibility =
            View.VISIBLE
        lay.visibility =
            View.GONE
        view.visibility =
            View.GONE
    }

    private fun captureImage() {
        val takePictureIntent =
            Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile: File?
        photoFile =
            createImageFile()
        val imageURI =
            FileProvider.getUriForFile(this@UploadImage, "com.siva.virtual.provider", photoFile!!)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI)
        activityResultLaunch.launch(takePictureIntent)
    }

    private fun createImageFile(): File? {
        // Create an image file name
        val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName =
            "JPEG_" + timeStamp + "_"
        val storageDir =
            getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        var image: File? =
            null
        try {
            image =
                File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",  /* suffix */
                    storageDir /* directory */
                )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        assert(image != null)
        file_path =
            image!!.path
        return image
    }

    private fun selectImage() {
        val gallery_intent =
            Intent()
        gallery_intent.type =
            "image/*"
        gallery_intent.action =
            Intent.ACTION_GET_CONTENT
        activityResultLaunch.launch(Intent.createChooser(gallery_intent, "Select Picture"))
    }

    private fun checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), MY_CAMERA_REQUEST_CODE)
            } else {
                captureImage()
            }
        } else captureImage()
    }

    companion object {
        private const val MY_CAMERA_REQUEST_CODE =
            100
        private const val MY_GALLERY_REQUEST_CODE =
            200
    }
}