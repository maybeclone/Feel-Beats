package com.silent.feelbeat.utils;

import android.graphics.Color;

/**
 * Created by silent on 7/17/2017.
 */

public class ColorUtils {

    public static int MATERIAL[] = {0xffe57373,
                                    0xfff06292,
                                    0xffba68c8,
                                    0xff9575cd,
                                    0xff7986cb,
                                    0xff64b5f6,
                                    0xff4fc3f7,
                                    0xff4dd0e1,
                                    0xff4db6ac,
                                    0xff81c784,
                                    0xffaed581,
                                    0xffff8a65,
                                    0xffd4e157,
                                    0xffffd54f,
                                    0xffffb74d,
                                    0xffa1887f,
                                    0xff90a4ae};

    public static final int COLOR_GREY = 0xffD3D3D3;

    public static int getBlackWhiteColor(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        if (darkness >= 0.5) {
            return Color.WHITE;
        } else return Color.BLACK;
    }

    public static int getColorStringLength(int length){
        int position = length % MATERIAL.length;
        return MATERIAL[position];
    }
}
