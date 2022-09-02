package com.siva.virtual;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.siva.virtual.databinding.ActivityAnswerSecurityQuestionBinding;
import com.siva.virtual.util.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AnswerSecurityQuestionActivity extends AppCompatActivity {

    private final List<String> questions = new ArrayList<>();
    private final List<String> answers = new ArrayList<>();
    private ActivityAnswerSecurityQuestionBinding binding;
    private int current_index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_answer_security_question);

        SharedPreferences sp = getSharedPreferences(Utils.FIRST_LOGIN_DETAILS,MODE_PRIVATE);
        answers.add(sp.getString(Utils.FIRST_ANSWER,null));
        answers.add(sp.getString(Utils.SECOND_ANSWER,null));
        answers.add(sp.getString(Utils.THIRD_ANSWER,null));

        questions.add(getResources().getString(R.string.first_question));
        questions.add(getResources().getString(R.string.second_question));
        questions.add(getResources().getString(R.string.third_question));

        binding.tvSecurityQuestion.setText(questions.get(current_index));

        binding.btnConfirm.setOnClickListener(view -> {
            String answer = Objects.requireNonNull(binding.etAnswer.getText()).toString().trim();
                if(answer.equals(answers.get(current_index)))
                {
                    Intent i = new Intent(AnswerSecurityQuestionActivity.this,PinSetUpActivity.class);
                    i.putExtra("from_where","from_answer");
                    startActivity(i);

                }else{
                    Utils.showErrorLay(AnswerSecurityQuestionActivity.this,binding.answerErrorLay.cardTop,
                            binding.answerErrorLay.errorLay,binding.answerErrorLay.tvInfo,"Entered answer was incorrect!");
                }
        });

        binding.answerErrorLay.fadeLay.setOnClickListener(view -> Utils.hideErrorLay(AnswerSecurityQuestionActivity.this,binding.answerErrorLay.cardTop,
                binding.answerErrorLay.errorLay));

        binding.tvTryAnother.setOnClickListener(view -> {
            binding.etAnswer.setText(null);
            current_index++;
           if(current_index == 3)
               current_index = 0;

            if(current_index <= 2){
                Animation animation = AnimationUtils.loadAnimation(AnswerSecurityQuestionActivity.this,R.anim.slide_in_right);
                binding.tvSecurityQuestion.startAnimation(animation);
                binding.tvSecurityQuestion.setText(questions.get(current_index));
            }

        });

        binding.etAnswer.setText(null);

        binding.etAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.btnConfirm.setEnabled(charSequence.length() > 0);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
}