package com.siva.virtual;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.databinding.DataBindingUtil;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import com.siva.virtual.databinding.ActivitySecretQuestionsBinding;
import com.siva.virtual.util.Utils;
import java.util.Objects;

public class SecretQuestionsActivity extends AppCompatActivity {

    private ActivitySecretQuestionsBinding binding;
    private boolean is_one_Ready = false,is_two_ready = false, is_three_ready = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_secret_questions);

        binding.btnSetup.setOnClickListener(view -> {
            String first = Objects.requireNonNull(binding.etFirstAnswer.getText()).toString().trim();
            String second = Objects.requireNonNull(binding.etSecondAnswer.getText()).toString().trim();
            String third = Objects.requireNonNull(binding.etThirdAnswer.getText()).toString().trim();

            SharedPreferences sp = getSharedPreferences(Utils.FIRST_LOGIN_DETAILS,MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(Utils.IS_FIRST_LOGIN,false);
            editor.apply();
            Utils.storeInSp(SecretQuestionsActivity.this,Utils.FIRST_ANSWER,first);
            Utils.storeInSp(SecretQuestionsActivity.this,Utils.SECOND_ANSWER,second);
            Utils.storeInSp(SecretQuestionsActivity.this,Utils.THIRD_ANSWER,third);

            Utils.moveToAnotherActivity(SecretQuestionsActivity.this,MainActivity.class);
        });

        binding.etFirstAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                is_one_Ready = charSequence.length() > 0;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                enableButton(binding.btnSetup);
            }
        });

        binding.etSecondAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                is_two_ready = charSequence.length() > 0;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                enableButton(binding.btnSetup);
            }
        });

        binding.etThirdAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                is_three_ready = charSequence.length() > 0;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                enableButton(binding.btnSetup);
            }
        });

        binding.lay.setOnClickListener(view -> {
            binding.etFirstAnswer.clearFocus();
            binding.etSecondAnswer.clearFocus();
            binding.etThirdAnswer.clearFocus();
            Utils.hideKeyBoard(SecretQuestionsActivity.this,view);
        });
    }

    private void enableButton(AppCompatButton btn) {
        btn.setEnabled(is_one_Ready && is_two_ready && is_three_ready);
    }
}