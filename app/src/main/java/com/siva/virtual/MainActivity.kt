package com.siva.virtual

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.siva.virtual.databinding.ActivityMainBinding
import com.siva.virtual.databinding.CardViewBinding
import com.siva.virtual.db.ImageDatabaseHelper
import com.siva.virtual.db.ImageViewModel
import com.siva.virtual.db.ImageViewModelFactory
import com.siva.virtual.db.Images
import com.siva.virtual.util.Utils

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val identity_list: MutableList<Images> =
        ArrayList()
    private val debit_list: MutableList<Images> =
        ArrayList()
    private val credit_list: MutableList<Images> =
        ArrayList()
    private val other_list: MutableList<Images> =
        ArrayList()
    private lateinit var imageViewModel: ImageViewModel

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        ActivityCompat.finishAffinity(this@MainActivity)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageDao = ImageDatabaseHelper.getDatabaseInstance(applicationContext).imageDao()
        val factory = ImageViewModelFactory(imageDao)
        imageViewModel = ViewModelProvider(this,factory)[ImageViewModel::class.java]

        imageViewModel.allImages.observe(this) { images: List<Images> ->
            for (im in images) {
                when (im.file_type) {
                    resources.getStringArray(R.array.file_types)[1] -> {
                        identity_list.add(im)
                    }
                    resources.getStringArray(R.array.file_types)[2] -> {
                        debit_list.add(im)
                    }
                    resources.getStringArray(R.array.file_types)[3] -> {
                        credit_list.add(im)
                    }
                    else -> {
                        other_list.add(im)
                    }
                }
            }
            setUpCorrespondingLays(
                identity_list,
                binding.layImg,
                binding.tvNoProofs,
                binding.proofFirstLay,
                binding.proofSecondLay
            )
            setUpCorrespondingLays(
                debit_list,
                binding.layImg1,
                binding.tvNoDebit,
                binding.debitFirstLay,
                binding.debitSecondLay
            )
            setUpCorrespondingLays(
                credit_list,
                binding.layImg2,
                binding.tvNoCredit,
                binding.creditFirstLay,
                binding.creditSecondLay
            )
            setUpCorrespondingLays(
                other_list,
                binding.layImg3,
                binding.tvNoOthers,
                binding.otherFirstLay,
                binding.otherSecondLay
            )
        }
        binding.ivPrivacy.setOnClickListener { view: View? ->
            Utils.moveToAnotherActivity(
                this@MainActivity,
                PrivacyActivity::class.java
            )
        }
        binding.proofOfIdentity.setOnClickListener { view: View? -> openImagesList("from_identity") }
        binding.debitCards.setOnClickListener { view: View? -> openImagesList("from_debit") }
        binding.creditCards.setOnClickListener { view: View? -> openImagesList("from_credit") }
        binding.Others.setOnClickListener { view: View? -> openImagesList("from_other") }
        binding.proofFirstLay.root.setOnClickListener { view: View? -> openFile(identity_list, 0) }
        binding.proofSecondLay.root.setOnClickListener { view: View? -> openFile(identity_list, 1) }
        binding.debitFirstLay.root.setOnClickListener { view: View? -> openFile(debit_list, 0) }
        binding.debitSecondLay.root.setOnClickListener { view: View? -> openFile(debit_list, 1) }
        binding.creditFirstLay.root.setOnClickListener { view: View? -> openFile(credit_list, 0) }
        binding.creditSecondLay.root.setOnClickListener { view: View? -> openFile(credit_list, 1) }
        binding.otherFirstLay.root.setOnClickListener { view: View? -> openFile(other_list, 0) }
        binding.otherSecondLay.root.setOnClickListener { view: View? -> openFile(other_list, 1) }
        binding.ivAdd.setOnClickListener { view: View? ->
            Utils.moveToAnotherActivity(
                this@MainActivity,
                UploadImage::class.java
            )
        }
    }

    private fun openImagesList(from: String) {
        val i =
            Intent(this@MainActivity, ImageListActivity::class.java)
        i.putExtra("from_which", from)
        startActivity(i)
    }

    private fun openFile(list: List<Images>, index: Int) {
        val i =
            Intent(this@MainActivity, OpenFileActivity::class.java)
        if (list[index].front_image != null) i.putExtra("front_image", list[index].front_image)
        if (list[index].back_image != null) i.putExtra("back_image", list[index].back_image)
        i.putExtra("file_name", list[index].file_name)
        i.putExtra("back_file_name", list[index].back_file_name)
        i.putExtra("id", list[index].id)
        startActivity(i)
    }

    private fun setUpCorrespondingLays(
        list: List<Images>,
        fullLay: LinearLayoutCompat,
        tv_info: TextView,
        firstLay: CardViewBinding,
        secondLay: CardViewBinding
    ) {
        if (list.isEmpty()) {
            tv_info.visibility =
                View.VISIBLE
        } else if (list.size == 1) {
            firstLay.fileName.text =
                list[0].file_name
            if (list[0].front_image != null) firstLay.docImage.setImageBitmap(
                Utils.loadImageFromStorage(
                    list[0].front_image, list[0].file_name, this, firstLay.docImage
                )
            ) else firstLay.docImage.setImageBitmap(
                Utils.loadImageFromStorage(
                    list[0].back_image, list[0].back_file_name, this, firstLay.docImage
                )
            )
            secondLay.cardTopLay.visibility =
                View.GONE
            fullLay.visibility =
                View.VISIBLE
        } else {
            firstLay.fileName.text =
                list[0].file_name
            if (list[0].front_image != null) firstLay.docImage.setImageBitmap(
                Utils.loadImageFromStorage(
                    list[0].front_image, list[0].file_name, this, firstLay.docImage
                )
            ) else firstLay.docImage.setImageBitmap(
                Utils.loadImageFromStorage(
                    list[0].back_image, list[0].back_file_name, this, firstLay.docImage
                )
            )
            secondLay.fileName.text =
                list[1].file_name
            if (list[1].front_image != null) secondLay.docImage.setImageBitmap(
                Utils.loadImageFromStorage(
                    list[1].front_image, list[1].file_name, this, secondLay.docImage
                )
            ) else secondLay.docImage.setImageBitmap(
                Utils.loadImageFromStorage(
                    list[1].back_image, list[1].back_file_name, this, secondLay.docImage
                )
            )
            fullLay.visibility =
                View.VISIBLE
        }
    }
}