package com.silent.feelbeat.customview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by silent on 7/20/2017.
 */

public class CustomTextView extends android.support.v7.widget.AppCompatTextView {

    public CustomTextView(Context context) {
        super(context);
        applyCustomFont(context, null);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context, null);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context, null);
    }

    private void applyCustomFont(Context context, AttributeSet attributeSet){
        Typeface font = Typeface.createFromAsset(context.getAssets(), "Blogger Sans.otf");
        setTypeface(font);
    }
}
