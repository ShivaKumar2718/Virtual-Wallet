package com.siva.virtual.util;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import com.siva.virtual.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Utils {
    public static final String FIRST_LOGIN_DETAILS = "isUserLoggedInFirstTime",
            IS_FIRST_LOGIN = "isFirstLogin", PIN = "PIN",USER_NAME = "user_name" ,
            FIRST_ANSWER = "first_answer", SECOND_ANSWER = "second_answer",
            THIRD_ANSWER = "third_answer";

    public static void storeInSp(Context context,String key,String data){
        SharedPreferences sp = context.getSharedPreferences(FIRST_LOGIN_DETAILS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key,data);
        editor.apply();
    }

    public static void moveToAnotherActivity(Context context, Class activityClass){
        Intent intent = new Intent(context,activityClass);
        context.startActivity(intent);
    }

    public static Bitmap loadImageFromStorage(String path, String file_name, Context context, AppCompatImageView docImage)
    {
        Bitmap b = null;
        try {
            File f=new File(path, file_name);
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            Snackbar.make(context,docImage,"Some of the files have deleted!",Snackbar.LENGTH_SHORT).show();
        }
         return b;
    }

    public static void hideKeyBoard(Context context,View view){
        InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    public static void showErrorLay(Context context, MaterialCardView card, View lay, AppCompatTextView tv,String value){
        tv.setText(value);
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.slide_down);
        card.startAnimation(anim);
        lay.setVisibility(View.VISIBLE);
    }

    public static void hideErrorLay(Context context, MaterialCardView card,View lay){
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.slide_up);
        card.startAnimation(anim);
        new Handler().postDelayed(() -> lay.setVisibility(View.GONE),600);

    }
}
