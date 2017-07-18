package com.silent.feelbeat.utils;

import android.graphics.Color;

import com.amulyakhare.textdrawable.util.ColorGenerator;

/**
 * Created by silent on 7/13/2017.
 */

public class SilentUtils {

    public static final String TITLE_FRAGMENT = "title";

    public static String getSubString(String string){
        String temp = string.charAt(1)>='a' && string.charAt(1)<='z' ? string.substring(0, 2) : string.substring(0, 1);
        return temp;
    }



}
