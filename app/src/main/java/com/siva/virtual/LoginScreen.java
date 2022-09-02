package com.siva.virtual;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import com.siva.virtual.databinding.ActivityLoginScreenBinding;
import com.siva.virtual.util.Utils;
import java.util.Objects;

public class LoginScreen extends AppCompatActivity {

    private ActivityLoginScreenBinding binding;
    private boolean ignore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_login_screen);

        SharedPreferences sp = getSharedPreferences(Utils.FIRST_LOGIN_DETAILS,MODE_PRIVATE);
        String name = sp.getString(Utils.USER_NAME,null);
        String pin = sp.getString(Utils.PIN,null);

        binding.tv1.setText(String.format("Welcome %s", name));


        binding.pinView.requestFocus();
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);

        binding.tvForgot.setOnClickListener(view -> {
            Utils.moveToAnotherActivity(LoginScreen.this, AnswerSecurityQuestionActivity.class);
            binding.pinView.setText(null);
        });

        binding.pinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (ignore)
                    return;
                if(charSequence.length() == 4){
                    if(pin.equals(Objects.requireNonNull(binding.pinView.getText()).toString().trim()))
                    {
                        Intent in = new Intent(LoginScreen.this,MainActivity.class);
                        startActivity(in);
                        finish();
                    }else{
                        Utils.showErrorLay(LoginScreen.this,binding.loginErrorLay.cardTop,binding.loginErrorLay.errorLay,
                                binding.loginErrorLay.tvInfo,"Entered PIN was incorrect!");
                        ignore=true;
                        binding.pinView.setText(null);
                        ignore=false;
                    }
                    Utils.hideKeyBoard(LoginScreen.this,binding.pinView);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.loginErrorLay.fadeLay.setOnClickListener(view -> Utils.hideErrorLay(LoginScreen.this,binding.loginErrorLay.cardTop,
                binding.loginErrorLay.errorLay));

        binding.loginLay.setOnClickListener(view -> Utils.hideKeyBoard(LoginScreen.this,view));

    }
}