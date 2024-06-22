package com.siva.virtualWallet

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.siva.virtualWallet.databinding.ActivityLoginScreenBinding
import com.siva.virtualWallet.util.Utils
import java.util.*

class LoginScreen : AppCompatActivity() {
    private lateinit var binding: ActivityLoginScreenBinding
    private var ignore =
        false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sp: SharedPreferences =
            getSharedPreferences(Utils.FIRST_LOGIN_DETAILS, Context.MODE_PRIVATE)
        val name: String? =
            sp.getString(Utils.USER_NAME, null)
        val pin: String? =
            sp.getString(Utils.PIN, null)
        binding.tv1.text =
            String.format("Welcome %s", name)
        binding.pinView.requestFocus()
        val im =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        binding.tvForgot.setOnClickListener { view ->
            Utils.moveToAnotherActivity(
                this@LoginScreen,
                AnswerSecurityQuestionActivity::class.java
            )
            binding.pinView.text =
                null
        }
        binding.pinView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (ignore) return
                if (charSequence.length == 4) {
                    if (pin == Objects.requireNonNull(binding.pinView.getText()).toString()
                            .trim { it <= ' ' }
                    ) {
                        val `in` =
                            Intent(this@LoginScreen, MainActivity::class.java)
                        startActivity(`in`)
                        finish()
                    } else {
                        Utils.showErrorLay(
                            this@LoginScreen,
                            binding.loginErrorLay.cardTop,
                            binding.loginErrorLay.errorLay,
                            binding.loginErrorLay.tvInfo,
                            "Entered PIN was incorrect!"
                        )
                        ignore =
                            true
                        binding.pinView.text =
                            null
                        ignore =
                            false
                    }
                    Utils.hideKeyBoard(this@LoginScreen, binding.pinView)
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        binding.loginErrorLay.fadeLay.setOnClickListener { view ->
            Utils.hideErrorLay(
                this@LoginScreen, binding.loginErrorLay.cardTop,
                binding.loginErrorLay.errorLay
            )
        }
        binding.loginLay.setOnClickListener { view -> Utils.hideKeyBoard(this@LoginScreen, view) }
    }
}