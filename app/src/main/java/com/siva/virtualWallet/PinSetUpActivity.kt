package com.siva.virtualWallet

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.siva.virtualWallet.databinding.ActivityPinSetUpBinding
import com.siva.virtualWallet.util.Utils
import java.util.*

class PinSetUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPinSetUpBinding
    private var first_pin: String? =
        null
    private var ignore =
        false
    private var toMain =
        false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPinSetUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideConfirmGroup()
        binding.pinViewSet.requestFocus()
        val im =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        if (getIntent().getExtras() != null) {
            toMain =
                true
        }
        binding.pinViewSet.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (ignore) {
                    return
                }
                Utils.hideErrorLay(
                    this@PinSetUpActivity, binding.setErrorLay.cardTop,
                    binding.setErrorLay.errorLay
                )
                if (charSequence.length == 4) {
                    first_pin =
                        Objects.requireNonNull(binding.pinViewSet.getText()).toString()
                            .trim { it <= ' ' }
                    hideSetGroup()
                    showConfirmGroup()
                    binding.pinViewConfirm.requestFocus()
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        binding.pinViewConfirm.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (ignore) {
                    return
                }
                if (charSequence.length == 4) {
                    if (first_pin == Objects.requireNonNull(binding.pinViewConfirm.getText())
                            .toString().trim { it <= ' ' }
                    ) {
                        val sp: SharedPreferences =
                            getSharedPreferences(Utils.FIRST_LOGIN_DETAILS, Context.MODE_PRIVATE)
                        val editor: SharedPreferences.Editor =
                            sp.edit()
                        editor.putString(Utils.PIN, first_pin)
                        editor.apply()
                        if (toMain) Utils.moveToAnotherActivity(
                            this@PinSetUpActivity,
                            MainActivity::class.java
                        ) else Utils.moveToAnotherActivity(
                            this@PinSetUpActivity,
                            SecretQuestionsActivity::class.java
                        )
                        Utils.hideKeyBoard(this@PinSetUpActivity, binding.pinViewConfirm)
                    } else {
                        Utils.showErrorLay(
                            this@PinSetUpActivity,
                            binding.setErrorLay.cardTop,
                            binding.setErrorLay.errorLay,
                            binding.setErrorLay.tvInfo,
                            "Both PIN's should match"
                        )
                        ignore =
                            true
                        binding.pinViewSet.setText(null)
                        binding.pinViewConfirm.setText(null)
                        ignore =
                            false
                        hideConfirmGroup()
                        showSetGroup()
                    }
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        binding.pinSetLay.setOnClickListener { view ->
            Utils.hideKeyBoard(this@PinSetUpActivity, view)
            binding.pinViewSet.clearFocus()
            binding.pinViewConfirm.clearFocus()
        }
        binding.setErrorLay.fadeLay.setOnClickListener { view ->
            Utils.hideErrorLay(
                this@PinSetUpActivity, binding.setErrorLay.cardTop,
                binding.setErrorLay.errorLay
            )
        }
    }

    private fun showSetGroup() {
        binding.setLayout.setVisibility(View.VISIBLE)
        val anim: Animation =
            AnimationUtils.loadAnimation(this, R.anim.slide_in_right)
        binding.setLayout.startAnimation(anim)
    }

    private fun showConfirmGroup() {
        val anim: Animation =
            AnimationUtils.loadAnimation(this, R.anim.slide_in_right)
        binding.confirmLayout.startAnimation(anim)
        binding.confirmLayout.setVisibility(View.VISIBLE)
    }

    private fun hideSetGroup() {
        binding.setLayout.setVisibility(View.GONE)
    }

    private fun hideConfirmGroup() {
        binding.confirmLayout.setVisibility(View.GONE)
    }
}