package com.siva.virtualWallet

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.siva.virtualWallet.util.Utils

class OpeningActivity : AppCompatActivity() {
    private var i: Intent? =
        null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val iv =
            findViewById<AppCompatImageView>(R.id.iv)
        val sp =
            getSharedPreferences(Utils.FIRST_LOGIN_DETAILS, MODE_PRIVATE)
        //checking whether user logging in for the first time or not
        i =
            if (sp.getBoolean(Utils.IS_FIRST_LOGIN, true)) {
                Intent(this@OpeningActivity, IntroActivity::class.java)
            } else Intent(this@OpeningActivity, LoginScreen::class.java)
        val anim =
            AnimationUtils.loadAnimation(this, R.anim.scale_out)
        iv.startAnimation(anim)
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                startActivity(i)
                finish()
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }
}