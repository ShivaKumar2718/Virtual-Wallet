package com.siva.virtualWallet.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.siva.virtualWallet.R
import com.siva.virtualWallet.db.Images
import com.siva.virtualWallet.util.Utils

class ImageAdapter(
    private val images: List<Images>,
    private val context: Context,
    val onImageClicks: onImageClick
) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(context).inflate(R.layout.scroll_card_view, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (images.isNotEmpty()) {
            holder.fileName.text =
                images[position].file_name
            if (images[position].front_image != null) {
                holder.front_image.setImageBitmap(
                    Utils.loadImageFromStorage(
                        images[position].front_image,
                        images[position].file_name,
                        context,
                        holder.front_image
                    )
                )
            } else {
                holder.front_image.visibility =
                    View.GONE
                holder.no_front.visibility =
                    View.VISIBLE
            }
            if (images[position].back_image != null) holder.back_image.setImageBitmap(
                Utils.loadImageFromStorage(
                    images[position].back_image,
                    images[position].back_file_name,
                    context,
                    holder.back_image
                )
            ) else {
                holder.back_image.visibility =
                    View.GONE
                holder.no_back.visibility =
                    View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        Log.d("Size", images.size.toString())
        return images.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val fileName: AppCompatTextView
        val no_front: AppCompatTextView
        val no_back: AppCompatTextView
        val front_image: AppCompatImageView
        val back_image: AppCompatImageView
        var onImageClick: onImageClick

        init {
            fileName =
                itemView.findViewById(R.id.file_name)
            front_image =
                itemView.findViewById(R.id.front_image)
            back_image =
                itemView.findViewById(R.id.back_image)
            no_front =
                itemView.findViewById(R.id.tv_no_front)
            no_back =
                itemView.findViewById(R.id.tv_no_back)
            onImageClick =
                onImageClicks
            itemView.setOnClickListener(this)
            front_image.setOnClickListener(this)
            back_image.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            onImageClick.onItemClick(adapterPosition, images)
        }
    }

    interface onImageClick {
        fun onItemClick(position: Int, image: List<Images>)
    }
}