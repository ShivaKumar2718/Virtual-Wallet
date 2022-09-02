package com.siva.virtual.adapter;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import com.siva.virtual.R;
import com.siva.virtual.model.Images;
import com.siva.virtual.util.Utils;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private final List<Images> images;
    private final Context context;
    public final onImageClick onImageClicks;

    public ImageAdapter(List<Images> images, Context context,onImageClick onImageClick) {
        this.images = images;
        this.context = context;
        this.onImageClicks = onImageClick;
    }

    @NonNull
    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.scroll_card_view,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ViewHolder holder, int position) {
        if(images.size() != 0){
            holder.fileName.setText(images.get(position).getFile_name());
            if(images.get(position).getFront_image() != null){
                holder.front_image.setImageBitmap(Utils.loadImageFromStorage(images.get(position).getFront_image(),images.get(position).getFile_name(),context, holder.front_image));
            }
            else{
                holder.front_image.setVisibility(View.GONE);
                holder.no_front.setVisibility(View.VISIBLE);
            }

            if(images.get(position).getBack_image() != null)
                holder.back_image.setImageBitmap(Utils.loadImageFromStorage(images.get(position).getBack_image(),images.get(position).getBack_file_name(),context, holder.back_image));
            else{
                holder.back_image.setVisibility(View.GONE);
                holder.no_back.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public int getItemCount() {
        Log.d("Size", String.valueOf(images.size()));
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final AppCompatTextView fileName,no_front,no_back;
        private final AppCompatImageView front_image, back_image;
        onImageClick onImageClick;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.file_name);
            front_image = itemView.findViewById(R.id.front_image);
            back_image = itemView.findViewById(R.id.back_image);
            no_front = itemView.findViewById(R.id.tv_no_front);
            no_back = itemView.findViewById(R.id.tv_no_back);
            this.onImageClick = onImageClicks;

            itemView.setOnClickListener(this);
            front_image.setOnClickListener(this);
            back_image.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onImageClick.onItemClick(getAdapterPosition(),images);
        }
    }


    public interface onImageClick {
        void onItemClick(int position,List<Images> image);
    }
}
