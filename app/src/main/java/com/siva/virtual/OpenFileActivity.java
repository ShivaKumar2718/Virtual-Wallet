package com.siva.virtual;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.siva.virtual.databinding.ActivityOpenFileBinding;
import com.siva.virtual.model.ImageViewModel;
import com.siva.virtual.util.Utils;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

public class OpenFileActivity extends AppCompatActivity {

    private ActivityOpenFileBinding binding;
    private Bitmap front_bitmap = null, back_bitmap = null, main_bitmap = null;
    private boolean is_front_available = false,is_main_front = false;
    private ImageViewModel imageViewModel;
    private String filename;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_open_file);

        imageViewModel = new ViewModelProvider.AndroidViewModelFactory(OpenFileActivity.this.getApplication())
                .create(ImageViewModel.class);

        if(getIntent().getExtras() != null){
            filename = getIntent().getStringExtra("file_name");
            id = getIntent().getIntExtra("id",0);
            binding.tvFileName.setText(filename);
            if(getIntent().getExtras().getString("front_image") != null)
            {
                is_front_available = true;
                is_main_front = true;
                front_bitmap = Utils.loadImageFromStorage(getIntent().getExtras().getString("front_image"),filename,this,binding.frontImg);
                binding.frontImg.setImageBitmap(front_bitmap);
                binding.frontImg.setVisibility(View.VISIBLE);
                binding.frontImg.setBackground(ContextCompat.getDrawable(this,R.drawable.image_highlight_drawable));
                binding.frontImg.setPadding(9,9,9,9);
                binding.mainImg.setImageBitmap(front_bitmap);
                main_bitmap = front_bitmap;
            }
            if(getIntent().getExtras().getString("back_image") != null){
                back_bitmap = Utils.loadImageFromStorage(getIntent().getExtras().getString("back_image"),getIntent().getExtras().getString("back_file_name"),this,binding.backImg);
                binding.backImg.setImageBitmap(back_bitmap);
                binding.backImg.setVisibility(View.VISIBLE);
                if(!is_front_available){
                    binding.mainImg.setImageBitmap(back_bitmap);
                    main_bitmap = back_bitmap;
                    binding.backImg.setBackground(ContextCompat.getDrawable(this,R.drawable.image_highlight_drawable));
                    binding.backImg.setPadding(9,9,9,9);
                }
            }
        }

        imageViewModel.updateCreatedDate(Calendar.getInstance().getTime(), id);

        binding.delete.setOnClickListener(view -> showAlert());

        binding.share.setOnClickListener(view -> {
            if(is_main_front)
            shareImage(front_bitmap,"front_"+filename);
            else
                shareImage(back_bitmap,"back_"+filename);
        });

        binding.rotate.setOnClickListener(view -> {
            main_bitmap = rotateBitmap(main_bitmap);
            binding.mainImg.setImageBitmap(rotateBitmap(main_bitmap));
        });

        binding.ivClose.setOnClickListener(view -> finish());

        binding.frontImg.setOnClickListener(view -> {
            binding.mainImg.setImageBitmap(front_bitmap);
            main_bitmap = front_bitmap;
            binding.frontImg.setPadding(9,9,9,9);
            binding.frontImg.setBackground(ContextCompat.getDrawable(OpenFileActivity.this,R.drawable.image_highlight_drawable));
            binding.backImg.setBackground(null);
            is_main_front = true;
            binding.backImg.setPadding(0,0,0,0);
        });

        binding.backImg.setOnClickListener(view -> {
            binding.mainImg.setImageBitmap(back_bitmap);
            main_bitmap = back_bitmap;
            binding.backImg.setBackground(ContextCompat.getDrawable(OpenFileActivity.this,R.drawable.image_highlight_drawable));
            binding.backImg.setPadding(9,9,9,9);
            binding.frontImg.setBackground(null);
            is_main_front = false;
            binding.frontImg.setPadding(0,0,0,0);
        });



        binding.mainImg.setOnClickListener(view -> {
            binding.topLay.setVisibility(binding.topLay.getVisibility()==View.VISIBLE ? slideUp(binding.topLay,R.anim.slide_up,View.GONE) : slideDown(binding.topLay,R.anim.slide_down,View.VISIBLE));
            binding.bottomLay.setVisibility(binding.bottomLay.getVisibility()==View.VISIBLE ? slideDown(binding.bottomLay, R.anim.slide_down_bottom,View.GONE) : slideUp(binding.bottomLay, R.anim.slide_up_bottom,View.VISIBLE));
        });
    }

    private Bitmap rotateBitmap(Bitmap front_bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(front_bitmap,0 , 0, front_bitmap.getWidth(), front_bitmap.getHeight(),matrix,true);
    }

    private int slideDown(LinearLayoutCompat lay, int slide_down,int visibility) {
        Animation anim = AnimationUtils.loadAnimation(OpenFileActivity.this,slide_down);
        lay.startAnimation(anim);
        lay.setVisibility(visibility);
        return lay.getVisibility();
    }

    private int slideUp(LinearLayoutCompat lay, int slide_up,int visibility) {
        Animation anim = AnimationUtils.loadAnimation(OpenFileActivity.this,slide_up);
        lay.startAnimation(anim);
        lay.setVisibility(visibility);
        return lay.getVisibility();
    }

    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(OpenFileActivity.this);
        builder.setTitle("Alert!");

        builder.setMessage("This will permanently delete this file. Still want to continue?");
        builder.setPositiveButton("YES", (dialogInterface, i) -> deleteFile(id));
        builder.setNegativeButton("NO", (dialogInterface, i) -> dialogInterface.dismiss());

        AlertDialog dialog =  builder.create();
        dialog.show();
    }

    private void deleteFile(int id) {
        imageViewModel.deleteById(id);
        Intent i = new Intent(OpenFileActivity.this,MainActivity.class);
        startActivity(i);
        finish();
    }

    private void shareImage(Bitmap bitmap, String s) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        Uri uri = getUri(bitmap,getApplicationContext(),s);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.putExtra(Intent.EXTRA_STREAM,uri);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT,"Virtual");
        startActivity(shareIntent);
    }

    private Uri getUri(Bitmap bitmap, Context applicationContext, String filename) {
        File image_folder = new File(applicationContext.getCacheDir(),"images");
        Uri uri = null;
        try{
            image_folder.mkdirs();
            File file = new File(image_folder,filename);
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(applicationContext,"com.siva.virtual"+".provider",file);
        }catch(Exception e){
            e.printStackTrace();
        }
        return uri;
    }
}