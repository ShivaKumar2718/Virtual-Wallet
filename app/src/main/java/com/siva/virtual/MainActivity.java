package com.siva.virtual;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.siva.virtual.databinding.ActivityMainBinding;
import com.siva.virtual.databinding.CardViewBinding;
import com.siva.virtual.model.ImageViewModel;
import com.siva.virtual.model.Images;
import com.siva.virtual.util.Utils;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private final List<Images> identity_list = new ArrayList<>();
    private final List<Images> debit_list = new ArrayList<>();
    private final List<Images> credit_list = new ArrayList<>();
    private final List<Images> other_list = new ArrayList<>();

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityCompat.finishAffinity(MainActivity.this);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        ImageViewModel imageViewModel = new ViewModelProvider.AndroidViewModelFactory(this.getApplication())
                .create(ImageViewModel.class);

        imageViewModel.getAllImages().observe(this, images -> {
            for( Images im : images){
                if(im.getFile_type().equals(getResources().getStringArray(R.array.file_types)[1])){
                    identity_list.add(im);
                }
                else if(im.getFile_type().equals(getResources().getStringArray(R.array.file_types)[2])){
                    debit_list.add(im);
                }
                else if(im.getFile_type().equals(getResources().getStringArray(R.array.file_types)[3])){
                    credit_list.add(im);
                }else{
                    other_list.add(im);
                }
            }

            setUpCorrespondingLays(identity_list,binding.layImg,binding.tvNoProofs,binding.proofFirstLay,binding.proofSecondLay);
            setUpCorrespondingLays(debit_list,binding.layImg1,binding.tvNoDebit,binding.debitFirstLay,binding.debitSecondLay);
            setUpCorrespondingLays(credit_list,binding.layImg2,binding.tvNoCredit,binding.creditFirstLay,binding.creditSecondLay);
            setUpCorrespondingLays(other_list,binding.layImg3,binding.tvNoOthers,binding.otherFirstLay,binding.otherSecondLay);
        });

        binding.ivPrivacy.setOnClickListener(view -> Utils.moveToAnotherActivity(MainActivity.this,PrivacyActivity.class));
        binding.proofOfIdentity.setOnClickListener(view -> openImagesList("from_identity"));

        binding.debitCards.setOnClickListener(view -> openImagesList("from_debit"));

        binding.creditCards.setOnClickListener(view -> openImagesList("from_credit"));

        binding.Others.setOnClickListener(view -> openImagesList("from_other"));

        binding.proofFirstLay.getRoot().setOnClickListener(view -> openFile(identity_list,0));

        binding.proofSecondLay.getRoot().setOnClickListener(view -> openFile(identity_list,1));
        binding.debitFirstLay.getRoot().setOnClickListener(view -> openFile(debit_list,0));
        binding.debitSecondLay.getRoot().setOnClickListener(view -> openFile(debit_list,1));
        binding.creditFirstLay.getRoot().setOnClickListener(view -> openFile(credit_list,0));
        binding.creditSecondLay.getRoot().setOnClickListener(view -> openFile(credit_list,1));

        binding.otherFirstLay.getRoot().setOnClickListener(view -> openFile(other_list,0));
        binding.otherSecondLay.getRoot().setOnClickListener(view -> openFile(other_list,1));

        binding.ivAdd.setOnClickListener(view -> Utils.moveToAnotherActivity(MainActivity.this,UploadImage.class));

    }

    private void openImagesList(String from) {
        Intent i = new Intent(MainActivity.this,ImageListActivity.class);
        i.putExtra("from_which", from);
        startActivity(i);
    }

    private void openFile(List<Images> list,int index) {
        Intent i = new Intent(MainActivity.this,OpenFileActivity.class);
        if(list.get(index).getFront_image() != null)
            i.putExtra("front_image",list.get(index).getFront_image());
        if(list.get(index).getBack_image() != null)
            i.putExtra("back_image",list.get(index).getBack_image());
        i.putExtra("file_name",list.get(index).getFile_name());
        i.putExtra("back_file_name",list.get(index).getBack_file_name());
        i.putExtra("id",list.get(index).getId());
        startActivity(i);
    }

    private void setUpCorrespondingLays(List<Images> list, LinearLayoutCompat fullLay, TextView tv_info
            , CardViewBinding firstLay, CardViewBinding secondLay) {

        if(list.size() == 0)
        {
            tv_info.setVisibility(View.VISIBLE);
        }else if(list.size() == 1)
        {
            firstLay.fileName.setText(list.get(0).getFile_name());
            if(list.get(0).getFront_image() != null)
            firstLay.docImage.setImageBitmap(Utils.loadImageFromStorage(list.get(0).getFront_image(),list.get(0).getFile_name(),this,firstLay.docImage));
            else
                firstLay.docImage.setImageBitmap(Utils.loadImageFromStorage(list.get(0).getBack_image(),list.get(0).getBack_file_name(),this,firstLay.docImage));
            secondLay.cardTopLay.setVisibility(View.GONE);
            fullLay.setVisibility(View.VISIBLE);
        }else
        {
            firstLay.fileName.setText(list.get(0).getFile_name());
            if(list.get(0).getFront_image() != null)
            firstLay.docImage.setImageBitmap(Utils.loadImageFromStorage(list.get(0).getFront_image(),list.get(0).getFile_name(),this,firstLay.docImage));
            else
                firstLay.docImage.setImageBitmap(Utils.loadImageFromStorage(list.get(0).getBack_image(),list.get(0).getBack_file_name(),this,firstLay.docImage));
            secondLay.fileName.setText(list.get(1).getFile_name());
            if(list.get(1).getFront_image() != null)
            secondLay.docImage.setImageBitmap(Utils.loadImageFromStorage(list.get(1).getFront_image(),list.get(1).getFile_name(),this,secondLay.docImage));
            else
                secondLay.docImage.setImageBitmap(Utils.loadImageFromStorage(list.get(1).getBack_image(),list.get(1).getBack_file_name(),this,secondLay.docImage));
            fullLay.setVisibility(View.VISIBLE);
        }
    }
}