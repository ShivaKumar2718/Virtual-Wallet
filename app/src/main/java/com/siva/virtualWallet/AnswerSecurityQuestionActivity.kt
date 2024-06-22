package com.siva.virtualWallet

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.siva.virtualWallet.databinding.ActivityAnswerSecurityQuestionBinding
import com.siva.virtualWallet.util.Utils
import java.util.*

class AnswerSecurityQuestionActivity : AppCompatActivity() {
    private val questions: MutableList<String> =
        ArrayList()
    private val answers: MutableList<String?> =
        ArrayList()
    private lateinit var binding: ActivityAnswerSecurityQuestionBinding
    private var current_index =
        0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnswerSecurityQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sp =
            getSharedPreferences(Utils.FIRST_LOGIN_DETAILS, MODE_PRIVATE)
        answers.add(sp.getString(Utils.FIRST_ANSWER, null))
        answers.add(sp.getString(Utils.SECOND_ANSWER, null))
        answers.add(sp.getString(Utils.THIRD_ANSWER, null))
        questions.add(resources.getString(R.string.first_question))
        questions.add(resources.getString(R.string.second_question))
        questions.add(resources.getString(R.string.third_question))

        binding.tvSecurityQuestion.text =
            questions[current_index]
        binding.btnConfirm.setOnClickListener { view: View? ->
            val answer =
                Objects.requireNonNull(binding.etAnswer.text).toString().trim { it <= ' ' }
            if (answer == answers[current_index]) {
                val i =
                    Intent(this@AnswerSecurityQuestionActivity, PinSetUpActivity::class.java)
                i.putExtra("from_where", "from_answer")
                startActivity(i)
            } else {
                Utils.showErrorLay(
                    this@AnswerSecurityQuestionActivity,
                    binding.answerErrorLay.cardTop,
                    binding.answerErrorLay.errorLay,
                    binding.answerErrorLay.tvInfo,
                    "Entered answer was incorrect!"
                )
            }
        }
        binding.answerErrorLay.fadeLay.setOnClickListener { view: View? ->
            Utils.hideErrorLay(
                this@AnswerSecurityQuestionActivity, binding.answerErrorLay.cardTop,
                binding.answerErrorLay.errorLay
            )
        }
        binding.tvTryAnother.setOnClickListener { view: View? ->
            binding.etAnswer.setText(null)
            current_index++
            if (current_index == 3) current_index =
                0
            if (current_index <= 2) {
                val animation =
                    AnimationUtils.loadAnimation(
                        this@AnswerSecurityQuestionActivity,
                        R.anim.slide_in_right
                    )
                binding.tvSecurityQuestion.startAnimation(animation)
                binding.tvSecurityQuestion.text =
                    questions[current_index]
            }
        }
        binding.etAnswer.setText(null)
        binding.etAnswer.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                binding.btnConfirm.isEnabled =
                    charSequence.length > 0
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }
}