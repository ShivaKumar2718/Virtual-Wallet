package com.siva.virtual;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import com.siva.virtual.databinding.ActivityUploadImageBinding;
import com.siva.virtual.model.ImageViewModel;
import com.siva.virtual.model.Images;
import com.siva.virtual.util.ImageFilePath;
import com.siva.virtual.util.Utils;
import com.google.android.material.snackbar.Snackbar;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class UploadImage extends AppCompatActivity{

    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final int MY_GALLERY_REQUEST_CODE = 200;
    private ActivityUploadImageBinding binding;
    public Bitmap front_bitmap, back_bitmap;
    private boolean from_upload = false,from_front_side = false;
    private String card_type, file_name = "", file_path;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_upload_image);

        ImageViewModel imageViewModel = new ViewModelProvider.AndroidViewModelFactory(this.getApplication())
                .create(ImageViewModel.class);

        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(this, R.array.file_types,
                        android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        binding.spinner.setAdapter(adapter);

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                card_type = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.btnSave.setOnClickListener(view -> {
            //save the file here
            if(!Objects.equals(card_type, getResources().getStringArray(R.array.file_types)[0]))
            {
                if(front_bitmap == null && back_bitmap == null){
                    Utils.showErrorLay(UploadImage.this,binding.uploadErrorLay.cardTop,binding.uploadErrorLay.errorLay,
                            binding.uploadErrorLay.tvInfo,"Atleast one side of the card is required!");
                }
                else{
                    if(Objects.requireNonNull(binding.etFileName.getText()).toString().trim().isEmpty())
                    {
                        Utils.showErrorLay(UploadImage.this,binding.uploadErrorLay.cardTop,binding.uploadErrorLay.errorLay,
                                binding.uploadErrorLay.tvInfo,"Filename please!");
                    }else
                    {
                        file_name = binding.etFileName.getText().toString().trim();
                        AsyncTask.execute(() -> {
                            if(imageViewModel.getCount(file_name)>0)
                                Snackbar.make(UploadImage.this,binding.btnSave,"Filename Already exists!",Snackbar.LENGTH_SHORT).show();
                            else{
                                storeImagesIntoDB();
                            }
                        });
                    }
                }
            }else{
                Utils.showErrorLay(UploadImage.this,binding.uploadErrorLay.cardTop,binding.uploadErrorLay.errorLay,
                        binding.uploadErrorLay.tvInfo,"Please select file type!");
            }
        });

        binding.uploadErrorLay.fadeLay.setOnClickListener(view -> Utils.hideErrorLay(UploadImage.this,binding.uploadErrorLay.cardTop,binding.uploadErrorLay.errorLay));

        binding.uploadLay.setOnClickListener(view -> Utils.hideKeyBoard(UploadImage.this,view));

        binding.camera.setOnClickListener(view -> {
            from_front_side = true;
            from_upload = false;
            binding.frontSideImg.setVisibility(View.VISIBLE);
            binding.frontView.setVisibility(View.VISIBLE);
            checkCameraPermission();
        });

        binding.upload.setOnClickListener(view -> {
            from_front_side = true;
            from_upload = true;
            binding.frontSideImg.setVisibility(View.VISIBLE);
            binding.frontView.setVisibility(View.VISIBLE);
            checkGalleryPermission();
        });

        binding.ivClose.setOnClickListener(view -> {
            binding.ivClose.setVisibility(View.GONE);
            binding.ivRotate.setVisibility(View.GONE);
            binding.frontSideImg.setImageBitmap(null);
            front_bitmap = null;
            binding.frontLay.setVisibility(View.VISIBLE);
        });

        binding.ivRotate.setOnClickListener(view -> {
            front_bitmap = rotateBitmap(front_bitmap);
            binding.frontSideImg.setImageBitmap(front_bitmap);
        });

        binding.cameraBack.setOnClickListener(view -> {
            from_front_side = false;
            from_upload = false;
            binding.backSideImg.setVisibility(View.VISIBLE);
            binding.backView.setVisibility(View.VISIBLE);
            checkCameraPermission();
        });

        binding.uploadBack.setOnClickListener(view -> {
            from_front_side = false;
            from_upload = true;
            binding.backSideImg.setVisibility(View.VISIBLE);
            binding.backView.setVisibility(View.VISIBLE);
            checkGalleryPermission();
        });

        binding.ivBackClose.setOnClickListener(view -> {
            binding.ivBackClose.setVisibility(View.GONE);
            binding.ivBackRotate.setVisibility(View.GONE);
            binding.backSideImg.setImageBitmap(null);
            back_bitmap = null;
            binding.backLay.setVisibility(View.VISIBLE);
        });

        binding.ivBackRotate.setOnClickListener(view -> {
            back_bitmap = rotateBitmap(back_bitmap);
            binding.backSideImg.setImageBitmap(back_bitmap);
        });
    }

    private Bitmap rotateBitmap(Bitmap front_bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(front_bitmap,0 , 0, front_bitmap.getWidth(), front_bitmap.getHeight(),matrix,true);
    }

    private void storeImagesIntoDB() {
        Images images = new Images();
        images.setFile_type(card_type);
        images.setFront_image(front_bitmap != null ? saveToInternalStorage(front_bitmap,file_name) : null);
        images.setBack_image(back_bitmap != null ? saveToInternalStorage(back_bitmap,"back_"+file_name) : null);
        images.setBack_file_name("back_"+file_name);
        images.setFile_name(file_name);
        images.setCreated_date(Calendar.getInstance().getTime());
        ImageViewModel.insertImage(images);
        Utils.moveToAnotherActivity(UploadImage.this,MainActivity.class);
    }

    private void checkGalleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_GALLERY_REQUEST_CODE);
            }else{
                selectImage();
            }
        }else
            selectImage();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MY_CAMERA_REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //permission granted
                captureImage();
            }else{
                if(from_front_side)
                {
                    binding.frontView.setVisibility(View.GONE);
                    binding.frontSideImg.setVisibility(View.GONE);
                }else{
                    binding.backView.setVisibility(View.GONE);
                    binding.backSideImg.setVisibility(View.GONE);
                }
                Utils.showErrorLay(UploadImage.this,binding.uploadErrorLay.cardTop,binding.uploadErrorLay.errorLay,
                        binding.uploadErrorLay.tvInfo,"Permission is needed to access the camera. Please allow it from the app settings");
            }

        }
        if(requestCode == MY_GALLERY_REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage();
            }else{
                if(from_front_side)
                {
                    binding.frontView.setVisibility(View.GONE);
                    binding.frontSideImg.setVisibility(View.GONE);
                }else{
                    binding.backView.setVisibility(View.GONE);
                    binding.backSideImg.setVisibility(View.GONE);
                }
                Utils.showErrorLay(UploadImage.this,binding.uploadErrorLay.cardTop,binding.uploadErrorLay.errorLay,
                        binding.uploadErrorLay.tvInfo,"Permission is needed to access the gallery. Please allow it from the app settings");
            }
        }
    }

    ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK)
                    {
                        if(!from_upload)
                        {
                            if(from_front_side){
                                front_bitmap = roteIfRequired(scalePic(binding.frontSideImg,file_path));
                                setImageOnScreen(front_bitmap,binding.frontSideImg, binding.ivClose, binding.frontLay, binding.ivRotate,binding.frontView);
                            }else{
                                back_bitmap = roteIfRequired(scalePic(binding.backSideImg,file_path));
                                setImageOnScreen(back_bitmap,binding.backSideImg, binding.ivBackClose, binding.backLay, binding.ivBackRotate,binding.backView);
                            }
                        }else{
                            //coming from upload
                            Uri imageUri = result.getData().getData();
                            if(from_front_side)
                            {
                                front_bitmap = scalePic(binding.frontSideImg, ImageFilePath.getPath(UploadImage.this,imageUri));
                                setImageOnScreen(front_bitmap,binding.frontSideImg, binding.ivClose, binding.frontLay, binding.ivRotate,binding.frontView);
                            }else{
                                back_bitmap = scalePic(binding.backSideImg, ImageFilePath.getPath(UploadImage.this,imageUri));
                                setImageOnScreen(back_bitmap,binding.backSideImg, binding.ivBackClose, binding.backLay,binding.ivBackRotate,binding.backView);
                            }
                        }
                    } else{
                        if(front_bitmap==null){
                            binding.frontSideImg.setVisibility(View.GONE);
                            binding.frontView.setVisibility(View.GONE);
                        }

                        if(back_bitmap == null){
                            binding.backSideImg.setVisibility(View.GONE);
                            binding.backView.setVisibility(View.GONE);
                        }
                    }
                }
            });

    private Bitmap roteIfRequired(Bitmap bitmap) {
        Bitmap result;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if(width>height)
           result = rotateBitmap(bitmap);
        else  result = bitmap;

        return result;
    }

    private Bitmap scalePic(AppCompatImageView imageView, String file_path) {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file_path, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = 0;

        // Determine how much to scale down the image
        try{
             scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }catch (Exception e){
            e.printStackTrace();
        }

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeFile(file_path, bmOptions);
    }

    private String saveToInternalStorage(Bitmap bitmapImage,String file_name){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,file_name);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert fos != null;
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    public void setImageOnScreen(Bitmap bitmap, AppCompatImageView iv1, AppCompatImageView iv2, LinearLayoutCompat lay, AppCompatImageView Rotate, View view) {
        iv1.setVisibility(View.VISIBLE);
        iv1.setImageBitmap(bitmap);
        iv2.setVisibility(View.VISIBLE);
        Rotate.setVisibility(View.VISIBLE);
        lay.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
    }


    private void captureImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile;
        photoFile = createImageFile();
        Uri imageURI = FileProvider.getUriForFile(UploadImage.this, "com.siva.virtual.provider", photoFile);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
        activityResultLaunch.launch(takePictureIntent);
    }

    private File createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Save a file: path for use with ACTION_VIEW intents
        assert image != null;
        file_path = image.getPath();
        return image;
    }

    private void selectImage() {
        Intent gallery_intent = new Intent();
        gallery_intent.setType("image/*");
        gallery_intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLaunch.launch(Intent.createChooser(gallery_intent,"Select Picture"));
    }

    private void checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
            }else{
                captureImage();
            }
        }else
            captureImage();

    }
}