package com.siva.virtual;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import com.siva.virtual.databinding.ActivityIntroBinding;
import com.siva.virtual.util.Utils;
import java.util.Objects;

public class IntroActivity extends AppCompatActivity {

    ActivityIntroBinding binding;
    private boolean isEtOk = false,isCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_intro);

        binding.btnDone.setOnClickListener(view -> {
            String name = Objects.requireNonNull(binding.etName.getText()).toString().trim();
                if(name.length() <= 10){
                    Utils.storeInSp(getApplicationContext(),Utils.USER_NAME,name);
                    Utils.moveToAnotherActivity(IntroActivity.this,PinSetUpActivity.class);
                }else
                    Utils.showErrorLay(IntroActivity.this,binding.introErrorLay.cardTop, binding.introErrorLay.errorLay,binding.introErrorLay.tvInfo,"Only 10 characters are allowed!");
        });

        binding.tvPolicy.setOnClickListener(view -> Utils.moveToAnotherActivity(IntroActivity.this,PrivacyActivity.class));

        binding.introErrorLay.fadeLay.setOnClickListener(view -> Utils.hideErrorLay(IntroActivity.this,binding.introErrorLay.cardTop,
                binding.introErrorLay.errorLay));

        binding.etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isEtOk = charSequence.length() > 0;
                enableButton();
                if(charSequence.length() == 11)
                    Utils.showErrorLay(IntroActivity.this,binding.introErrorLay.cardTop,binding.introErrorLay.errorLay,binding.introErrorLay.tvInfo,"Only 10 characters are allowed!");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.agreeCheck.setOnCheckedChangeListener((compoundButton, b) -> {
            isCheck = compoundButton.isChecked();
            enableButton();
        });

    }

    private void enableButton() {
        binding.btnDone.setEnabled(isCheck && isEtOk);
    }
}