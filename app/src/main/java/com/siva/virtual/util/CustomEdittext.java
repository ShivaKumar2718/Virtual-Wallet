package com.siva.virtual.util;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

public class CustomEdittext extends androidx.appcompat.widget.AppCompatEditText {

    public CustomEdittext(Context context) {
        super(context);
    }

    public CustomEdittext(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomEdittext(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if ( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP )
            this.clearFocus();
        return super.onKeyPreIme(keyCode, event);
    }
}
