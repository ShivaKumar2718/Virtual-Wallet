package com.siva.virtual

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.siva.virtual.databinding.ActivityOpenFileBinding
import com.siva.virtual.db.ImageDatabaseHelper
import com.siva.virtual.db.ImageViewModel
import com.siva.virtual.db.ImageViewModelFactory
import com.siva.virtual.util.Utils
import java.io.File
import java.io.FileOutputStream
import java.util.*

class OpenFileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOpenFileBinding
    private var front_bitmap: Bitmap? =
        null
    private var back_bitmap: Bitmap? =
        null
    private var main_bitmap: Bitmap? =
        null
    private var is_front_available =
        false
    private var is_main_front =
        false
    private lateinit var imageViewModel: ImageViewModel
    private var filename: String? =
        null
    private var id =
        0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpenFileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageDao = ImageDatabaseHelper.getDatabaseInstance(applicationContext).imageDao()
        val factory = ImageViewModelFactory(imageDao)
        imageViewModel = ViewModelProvider(this,factory)[ImageViewModel::class.java]

        if (intent.extras != null) {
            filename =
                intent.getStringExtra("file_name")
            id =
                intent.getIntExtra("id", 0)
            binding.tvFileName.text =
                filename
            if (intent.extras!!.getString("front_image") != null) {
                is_front_available =
                    true
                is_main_front =
                    true
                front_bitmap =
                    Utils.loadImageFromStorage(
                        intent.extras!!.getString("front_image"), filename, this, binding.frontImg
                    )
                binding.frontImg.setImageBitmap(front_bitmap)
                binding.frontImg.visibility =
                    View.VISIBLE
                binding.frontImg.background =
                    ContextCompat.getDrawable(this, R.drawable.image_highlight_drawable)
                binding.frontImg.setPadding(9, 9, 9, 9)
                binding.mainImg.setImageBitmap(front_bitmap)
                main_bitmap =
                    front_bitmap
            }
            if (intent.extras!!.getString("back_image") != null) {
                back_bitmap =
                    Utils.loadImageFromStorage(
                        intent.extras!!.getString("back_image"),
                        intent.extras!!.getString("back_file_name"),
                        this,
                        binding.backImg
                    )
                binding.backImg.setImageBitmap(back_bitmap)
                binding.backImg.visibility =
                    View.VISIBLE
                if (!is_front_available) {
                    binding.mainImg.setImageBitmap(back_bitmap)
                    main_bitmap =
                        back_bitmap
                    binding.backImg.background =
                        ContextCompat.getDrawable(this, R.drawable.image_highlight_drawable)
                    binding.backImg.setPadding(9, 9, 9, 9)
                }
            }
        }

        imageViewModel.updateCreatedDate(Calendar.getInstance().time, id)

        binding.delete.setOnClickListener { view: View? -> showAlert() }
        binding.share.setOnClickListener { view: View? ->
            if (is_main_front) shareImage(
                front_bitmap,
                "front_$filename"
            ) else shareImage(back_bitmap, "back_$filename")
        }
        binding.rotate.setOnClickListener { view: View? ->
            main_bitmap =
                rotateBitmap(main_bitmap)
            binding.mainImg.setImageBitmap(rotateBitmap(main_bitmap))
        }
        binding.ivClose.setOnClickListener { view: View? -> finish() }
        binding.frontImg.setOnClickListener { view: View? ->
            binding.mainImg.setImageBitmap(front_bitmap)
            main_bitmap =
                front_bitmap
            binding.frontImg.setPadding(9, 9, 9, 9)
            binding.frontImg.background =
                ContextCompat.getDrawable(
                    this@OpenFileActivity,
                    R.drawable.image_highlight_drawable
                )
            binding.backImg.background =
                null
            is_main_front =
                true
            binding.backImg.setPadding(0, 0, 0, 0)
        }
        binding.backImg.setOnClickListener { view: View? ->
            binding.mainImg.setImageBitmap(back_bitmap)
            main_bitmap =
                back_bitmap
            binding.backImg.background =
                ContextCompat.getDrawable(
                    this@OpenFileActivity,
                    R.drawable.image_highlight_drawable
                )
            binding.backImg.setPadding(9, 9, 9, 9)
            binding.frontImg.background =
                null
            is_main_front =
                false
            binding.frontImg.setPadding(0, 0, 0, 0)
        }
        binding.mainImg.setOnClickListener { view: View? ->
            binding.topLay.visibility =
                if (binding.topLay.visibility == View.VISIBLE) slideUp(
                    binding.topLay,
                    R.anim.slide_up,
                    View.GONE
                ) else slideDown(binding.topLay, R.anim.slide_down, View.VISIBLE)
            binding.bottomLay.visibility =
                if (binding.bottomLay.visibility == View.VISIBLE) slideDown(
                    binding.bottomLay,
                    R.anim.slide_down_bottom,
                    View.GONE
                ) else slideUp(binding.bottomLay, R.anim.slide_up_bottom, View.VISIBLE)
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

    private fun slideDown(lay: LinearLayoutCompat, slide_down: Int, visibility: Int): Int {
        val anim =
            AnimationUtils.loadAnimation(this@OpenFileActivity, slide_down)
        lay.startAnimation(anim)
        lay.visibility =
            visibility
        return lay.visibility
    }

    private fun slideUp(lay: LinearLayoutCompat, slide_up: Int, visibility: Int): Int {
        val anim =
            AnimationUtils.loadAnimation(this@OpenFileActivity, slide_up)
        lay.startAnimation(anim)
        lay.visibility =
            visibility
        return lay.visibility
    }

    private fun showAlert() {
        val builder =
            AlertDialog.Builder(this@OpenFileActivity)
        builder.setTitle("Alert!")
        builder.setMessage("This will permanently delete this file. Still want to continue?")
        builder.setPositiveButton("YES") { dialogInterface: DialogInterface?, i: Int ->
            deleteFile(
                id
            )
        }
        builder.setNegativeButton("NO") { dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss() }
        val dialog =
            builder.create()
        dialog.show()
    }

    private fun deleteFile(id: Int) {
        imageViewModel!!.deleteById(id)
        val i =
            Intent(this@OpenFileActivity, MainActivity::class.java)
        startActivity(i)
        finish()
    }

    private fun shareImage(bitmap: Bitmap?, s: String) {
        val shareIntent =
            Intent(Intent.ACTION_SEND)
        shareIntent.type =
            "image/*"
        val uri =
            getUri(bitmap, applicationContext, s)
        shareIntent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Virtual")
        startActivity(shareIntent)
    }

    private fun getUri(bitmap: Bitmap?, applicationContext: Context, filename: String): Uri? {
        val image_folder =
            File(applicationContext.cacheDir, "images")
        var uri: Uri? =
            null
        try {
            image_folder.mkdirs()
            val file =
                File(image_folder, filename)
            val stream =
                FileOutputStream(file)
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
            uri =
                FileProvider.getUriForFile(
                    applicationContext,
                    "com.siva.virtual" + ".provider",
                    file
                )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return uri
    }
}