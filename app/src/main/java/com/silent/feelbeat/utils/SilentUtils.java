package com.silent.feelbeat.utils;

import android.content.res.Resources;
import android.graphics.Color;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.silent.feelbeat.R;

import java.util.concurrent.TimeUnit;

/**
 * Created by silent on 7/13/2017.
 */

public class SilentUtils {

    public static final String TITLE_FRAGMENT = "title";

    public static String getSubString(String string){
        String temp = string.charAt(1)>='a' && string.charAt(1)<='z' ? string.substring(0, 2) : string.substring(0, 1);
        return temp;
    }

    public static final String getStringTimeFromDuration(long miliseconds){
        int hours = (int) TimeUnit.MILLISECONDS.toHours(miliseconds);
        int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(miliseconds) - hours*60;
        int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(miliseconds) - minutes*60;
        if(hours == 0){
            return String.format(Resources.getSystem().getString(R.string.format_time_song_1),
                    miliseconds, seconds);
        }
        return String.format(Resources.getSystem().getString(R.string.format_time_song_2),
                hours, miliseconds, seconds);
    }


}
