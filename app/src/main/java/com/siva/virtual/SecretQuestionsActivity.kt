package com.siva.virtual

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.siva.virtual.databinding.ActivitySecretQuestionsBinding
import com.siva.virtual.util.Utils
import java.util.*

class SecretQuestionsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySecretQuestionsBinding
    private var is_one_Ready =
        false
    private var is_two_ready =
        false
    private var is_three_ready =
        false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecretQuestionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnSetup.setOnClickListener { view: View? ->
            val first =
                Objects.requireNonNull(binding.etFirstAnswer.text).toString().trim { it <= ' ' }
            val second =
                Objects.requireNonNull(binding.etSecondAnswer.text).toString().trim { it <= ' ' }
            val third =
                Objects.requireNonNull(binding.etThirdAnswer.text).toString().trim { it <= ' ' }
            val sp =
                getSharedPreferences(Utils.FIRST_LOGIN_DETAILS, MODE_PRIVATE)
            val editor =
                sp.edit()
            editor.putBoolean(Utils.IS_FIRST_LOGIN, false)
            editor.apply()
            Utils.storeInSp(this@SecretQuestionsActivity, Utils.FIRST_ANSWER, first)
            Utils.storeInSp(this@SecretQuestionsActivity, Utils.SECOND_ANSWER, second)
            Utils.storeInSp(this@SecretQuestionsActivity, Utils.THIRD_ANSWER, third)
            Utils.moveToAnotherActivity(this@SecretQuestionsActivity, MainActivity::class.java)
        }
        binding.etFirstAnswer.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                is_one_Ready =
                    charSequence.length > 0
            }

            override fun afterTextChanged(editable: Editable) {
                enableButton(binding.btnSetup)
            }
        })
        binding.etSecondAnswer.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                is_two_ready =
                    charSequence.length > 0
            }

            override fun afterTextChanged(editable: Editable) {
                enableButton(binding.btnSetup)
            }
        })
        binding.etThirdAnswer.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                is_three_ready =
                    charSequence.length > 0
            }

            override fun afterTextChanged(editable: Editable) {
                enableButton(binding.btnSetup)
            }
        })
        binding.lay.setOnClickListener { view: View ->
            binding.etFirstAnswer.clearFocus()
            binding.etSecondAnswer.clearFocus()
            binding.etThirdAnswer.clearFocus()
            Utils.hideKeyBoard(this@SecretQuestionsActivity, view)
        }
    }

    private fun enableButton(btn: AppCompatButton) {
        btn.isEnabled =
            is_one_Ready && is_two_ready && is_three_ready
    }
}