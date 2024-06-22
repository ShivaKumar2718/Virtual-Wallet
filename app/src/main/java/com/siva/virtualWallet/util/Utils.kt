package com.siva.virtualWallet.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.siva.virtualWallet.R
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

object Utils {
    const val FIRST_LOGIN_DETAILS =
        "isUserLoggedInFirstTime"
    const val IS_FIRST_LOGIN =
        "isFirstLogin"
    const val PIN =
        "PIN"
    const val USER_NAME =
        "user_name"
    const val FIRST_ANSWER =
        "first_answer"
    const val SECOND_ANSWER =
        "second_answer"
    const val THIRD_ANSWER =
        "third_answer"

    fun storeInSp(context: Context, key: String?, data: String?) {
        val sp =
            context.getSharedPreferences(FIRST_LOGIN_DETAILS, Context.MODE_PRIVATE)
        val editor =
            sp.edit()
        editor.putString(key, data)
        editor.apply()
    }

    fun moveToAnotherActivity(context: Context, activityClass: Class<*>?) {
        val intent =
            Intent(context, activityClass)
        context.startActivity(intent)
    }

    fun loadImageFromStorage(
        path: String?,
        file_name: String?,
        context: Context?,
        docImage: AppCompatImageView?
    ): Bitmap? {
        var b: Bitmap? =
            null
        try {
            val f =
                file_name?.let { File(path, it) }
            b =
                BitmapFactory.decodeStream(FileInputStream(f))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Snackbar.make(
                context!!,
                docImage!!,
                "Some of the files have deleted!",
                Snackbar.LENGTH_SHORT
            ).show()
        }
        return b
    }

    fun hideKeyBoard(context: Context, view: View) {
        val im =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showErrorLay(
        context: Context?,
        card: MaterialCardView,
        lay: View,
        tv: AppCompatTextView,
        value: String?
    ) {
        tv.text =
            value
        val anim =
            AnimationUtils.loadAnimation(context, R.anim.slide_down)
        card.startAnimation(anim)
        lay.visibility =
            View.VISIBLE
    }

    fun hideErrorLay(context: Context?, card: MaterialCardView, lay: View) {
        val anim =
            AnimationUtils.loadAnimation(context, R.anim.slide_up)
        card.startAnimation(anim)
        Handler().postDelayed({
            lay.visibility =
                View.GONE
        }, 600)
    }
}