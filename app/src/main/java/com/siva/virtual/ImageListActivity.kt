package com.siva.virtual

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.siva.virtual.adapter.ImageAdapter
import com.siva.virtual.adapter.ImageAdapter.onImageClick
import com.siva.virtual.db.ImageDatabaseHelper
import com.siva.virtual.db.ImageViewModel
import com.siva.virtual.db.ImageViewModelFactory
import com.siva.virtual.db.Images

class ImageListActivity : AppCompatActivity(), onImageClick {
    private lateinit var recyclerView: RecyclerView
    private var imageAdapter: ImageAdapter? =
        null
    private lateinit var tv_no_cards: AppCompatTextView
    private var from: String? =
        null
    private val images: MutableList<Images> =
        ArrayList()
    private var isExecute =
        true
    private lateinit var imageViewModel: ImageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_list)
        tv_no_cards =
            findViewById(R.id.tv_no_cards)
        val searchView =
            findViewById<SearchView>(R.id.search_box)
        val iv_back =
            findViewById<AppCompatImageView>(R.id.iv_back)
        val imageDao = ImageDatabaseHelper.getDatabaseInstance(applicationContext).imageDao()
        val factory = ImageViewModelFactory(imageDao)
        imageViewModel = ViewModelProvider(this,factory)[ImageViewModel::class.java]
        recyclerView =
            findViewById(R.id.recyclerView)
        recyclerView.setLayoutManager(LinearLayoutManager(this))
        recyclerView.setHasFixedSize(true)
        if (intent.extras != null) {
            from =
                intent.getStringExtra("from_which")
            isExecute =
                false
        }
        imageViewModel.allImages.observe(this) { list: List<Images> ->
            if (!isExecute) {
                for (im in list) {
                    if (from == "from_identity" && im.file_type == resources.getStringArray(R.array.file_types)[1]) {
                        images.add(im)
                    } else if (from == "from_debit" && im.file_type == resources.getStringArray(R.array.file_types)[2]) {
                        images.add(im)
                    } else if (from == "from_credit" && im.file_type == resources.getStringArray(R.array.file_types)[3]) {
                        images.add(im)
                    } else if (from == "from_other" && im.file_type == resources.getStringArray(R.array.file_types)[4]) {
                        images.add(im)
                    }
                }
                if (images.size == 0) tv_no_cards.setVisibility(View.VISIBLE) else {
                    imageAdapter =
                        ImageAdapter(images, this@ImageListActivity, this@ImageListActivity)
                    recyclerView.setAdapter(imageAdapter)
                    Log.d("What", "here there")
                }
            }
        }
        searchView.setOnClickListener { view: View? ->
            searchView.setIconifiedByDefault(true)
            searchView.isFocusable =
                true
            searchView.isIconified =
                false
            searchView.requestFocusFromTouch()
            searchView.background =
                ResourcesCompat.getDrawable(resources, R.drawable.blue_outline, theme)
        }
        searchView.setOnCloseListener {
            searchView.background =
                ResourcesCompat.getDrawable(resources, R.drawable.et_background, theme)
            false
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                val filtered_images: MutableList<Images> =
                    ArrayList()
                for (i in images.indices) {
                    if (images[i].file_name?.contains(newText) == true) {
                        filtered_images.add(images[i])
                    }
                }
                imageAdapter =
                    ImageAdapter(filtered_images, this@ImageListActivity, this@ImageListActivity)
                recyclerView.setAdapter(imageAdapter)
                return true
            }
        })
        iv_back.setOnClickListener { view: View? -> finish() }
    }

    override fun onItemClick(position: Int, image: List<Images>) {
        isExecute =
            true
        val i =
            Intent(this@ImageListActivity, OpenFileActivity::class.java)
        if (image[position].front_image != null) i.putExtra(
            "front_image",
            image[position].front_image
        )
        if (image[position].back_image != null) i.putExtra("back_image", image[position].back_image)
        i.putExtra("file_name", image[position].file_name)
        i.putExtra("back_file_name", image[position].back_file_name)
        i.putExtra("id", image[position].id)
        startActivity(i)
    }

}