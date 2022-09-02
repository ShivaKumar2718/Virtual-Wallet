package com.siva.virtual;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import com.siva.virtual.databinding.ActivityPinSetUpBinding;
import com.siva.virtual.util.Utils;
import java.util.Objects;

public class PinSetUpActivity extends AppCompatActivity {
    private ActivityPinSetUpBinding binding;
    private String first_pin;
    private boolean ignore = false,toMain = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_pin_set_up);

        hideConfirmGroup();

        binding.pinViewSet.requestFocus();
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);


        if(getIntent().getExtras() != null){
            toMain = true;
        }

        binding.pinViewSet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (ignore){
                    return;
                }
                Utils.hideErrorLay(PinSetUpActivity.this,binding.setErrorLay.cardTop,
                        binding.setErrorLay.errorLay);
                if(charSequence.length() == 4){
                    first_pin = Objects.requireNonNull(binding.pinViewSet.getText()).toString().trim();
                    hideSetGroup();
                    showConfirmGroup();
                    binding.pinViewConfirm.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        binding.pinViewConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (ignore){
                    return;
                }
                if(charSequence.length() == 4){
                    if(first_pin.equals(Objects.requireNonNull(binding.pinViewConfirm.getText()).toString().trim()))
                    {
                        SharedPreferences sp = getSharedPreferences(Utils.FIRST_LOGIN_DETAILS,MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString(Utils.PIN,first_pin);
                        editor.apply();
                        if(toMain)
                            Utils.moveToAnotherActivity(PinSetUpActivity.this,MainActivity.class);
                        else
                        Utils.moveToAnotherActivity(PinSetUpActivity.this,SecretQuestionsActivity.class);

                        Utils.hideKeyBoard(PinSetUpActivity.this,binding.pinViewConfirm);

                    }else{
                        Utils.showErrorLay(PinSetUpActivity.this,binding.setErrorLay.cardTop,binding.setErrorLay.errorLay,
                                binding.setErrorLay.tvInfo,"Both PIN's should match");
                        ignore = true;
                        binding.pinViewSet.setText(null);
                        binding.pinViewConfirm.setText(null);
                        ignore = false;
                        hideConfirmGroup();
                        showSetGroup();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
       binding.pinSetLay.setOnClickListener(view -> {
           Utils.hideKeyBoard(PinSetUpActivity.this,view);
           binding.pinViewSet.clearFocus();
           binding.pinViewConfirm.clearFocus();
       });

        binding.setErrorLay.fadeLay.setOnClickListener(view -> Utils.hideErrorLay(PinSetUpActivity.this,binding.setErrorLay.cardTop,
                binding.setErrorLay.errorLay));
    }

    private void showSetGroup() {
       binding.setLayout.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(this,R.anim.slide_in_right);
        binding.setLayout.startAnimation(anim);
    }

    private void showConfirmGroup() {
        Animation anim = AnimationUtils.loadAnimation(this,R.anim.slide_in_right);
        binding.confirmLayout.startAnimation(anim);
        binding.confirmLayout.setVisibility(View.VISIBLE);
    }

    private void hideSetGroup() {
        binding.setLayout.setVisibility(View.GONE);
    }
    private void hideConfirmGroup() {
        binding.confirmLayout.setVisibility(View.GONE);
    }

}