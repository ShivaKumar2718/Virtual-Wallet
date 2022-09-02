package com.siva.virtual;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.siva.virtual.adapter.ImageAdapter;
import com.siva.virtual.model.ImageViewModel;
import com.siva.virtual.model.Images;
import java.util.ArrayList;
import java.util.List;

public class ImageListActivity extends AppCompatActivity implements ImageAdapter.onImageClick{
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private AppCompatTextView tv_no_cards;
    private String from;
    private final List<Images> images = new ArrayList<>();
    private boolean isExecute = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);

        tv_no_cards = findViewById(R.id.tv_no_cards);
        SearchView searchView = findViewById(R.id.search_box);
        AppCompatImageView iv_back = findViewById(R.id.iv_back);

        ImageViewModel imageViewModel = new ViewModelProvider.AndroidViewModelFactory(ImageListActivity.this.getApplication())
                .create(ImageViewModel.class);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        if(getIntent().getExtras() != null){
            from = getIntent().getStringExtra("from_which");
            isExecute = false;
        }

        imageViewModel.getAllImages().observe(this, list -> {
            if (!isExecute){
                for (Images im : list)
                {
                    if(from.equals("from_identity") && im.getFile_type().equals(getResources().getStringArray(R.array.file_types)[1]))
                    {
                        images.add(im);
                    }
                    else if(from.equals("from_debit") && im.getFile_type().equals(getResources().getStringArray(R.array.file_types)[2]))
                    {
                        images.add(im);
                    }
                    else if(from.equals("from_credit") && im.getFile_type().equals(getResources().getStringArray(R.array.file_types)[3]))
                    {
                        images.add(im);
                    }else if(from.equals("from_other") && im.getFile_type().equals(getResources().getStringArray(R.array.file_types)[4])){
                        images.add(im);
                    }
                }
                if(images.size() == 0)
                    tv_no_cards.setVisibility(View.VISIBLE);
                else{
                    imageAdapter = new ImageAdapter(images,ImageListActivity.this,ImageListActivity.this);
                    recyclerView.setAdapter(imageAdapter);
                    Log.d("What","here there");
                }
            }

        });

        searchView.setOnClickListener(view -> {
            searchView.setIconifiedByDefault(true);
            searchView.setFocusable(true);
            searchView.setIconified(false);
            searchView.requestFocusFromTouch();
            searchView.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.blue_outline,getTheme()));
        });

        searchView.setOnCloseListener(() -> {
            searchView.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.et_background,getTheme()));
            return false;
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<Images> filtered_images = new ArrayList<>();
                for(int i=0; i<images.size(); i++){
                    if(images.get(i).getFile_name().contains(newText)){
                        filtered_images.add(images.get(i));
                    }
                }
                imageAdapter = new ImageAdapter(filtered_images,ImageListActivity.this, ImageListActivity.this);
                recyclerView.setAdapter(imageAdapter);
                return true;
            }
        });

        iv_back.setOnClickListener(view -> finish());

    }

    @Override
    public void onItemClick(int position,List<Images> image) {
        isExecute = true;
        Intent i = new Intent(ImageListActivity.this,OpenFileActivity.class);
        if(image.get(position).getFront_image() != null)
            i.putExtra("front_image",image.get(position).getFront_image());
        if(image.get(position).getBack_image() != null)
            i.putExtra("back_image",image.get(position).getBack_image());
        i.putExtra("file_name",image.get(position).getFile_name());
        i.putExtra("back_file_name",image.get(position).getBack_file_name());
        i.putExtra("id",image.get(position).getId());
        startActivity(i);
    }
}