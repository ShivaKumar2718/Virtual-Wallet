package com.siva.virtualWallet

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import com.siva.virtualWallet.databinding.ActivityIntroBinding
import com.siva.virtualWallet.util.Utils
import java.util.*

class IntroActivity : AppCompatActivity() {
    lateinit var binding: ActivityIntroBinding
    private var isEtOk =
        false
    private var isCheck =
        false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnDone.setOnClickListener { view: View? ->
            val name =
                Objects.requireNonNull(binding.etName.text).toString().trim { it <= ' ' }
            if (name.length <= 10) {
                Utils.storeInSp(applicationContext, Utils.USER_NAME, name)
                Utils.moveToAnotherActivity(this@IntroActivity, PinSetUpActivity::class.java)
            } else Utils.showErrorLay(
                this@IntroActivity,
                binding.introErrorLay.cardTop,
                binding.introErrorLay.errorLay,
                binding.introErrorLay.tvInfo,
                "Only 10 characters are allowed!"
            )
        }
        binding.tvPolicy.setOnClickListener { view: View? ->
            Utils.moveToAnotherActivity(
                this@IntroActivity,
                PrivacyActivity::class.java
            )
        }
        binding.introErrorLay.fadeLay.setOnClickListener { view: View? ->
            Utils.hideErrorLay(
                this@IntroActivity, binding.introErrorLay.cardTop,
                binding.introErrorLay.errorLay
            )
        }
        binding.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                isEtOk =
                    charSequence.length > 0
                enableButton()
                if (charSequence.length == 11) Utils.showErrorLay(
                    this@IntroActivity,
                    binding.introErrorLay.cardTop,
                    binding.introErrorLay.errorLay,
                    binding.introErrorLay.tvInfo,
                    "Only 10 characters are allowed!"
                )
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        binding.agreeCheck.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
            isCheck =
                compoundButton.isChecked
            enableButton()
        }
    }

    private fun enableButton() {
        binding!!.btnDone.isEnabled =
            isCheck && isEtOk
    }
}