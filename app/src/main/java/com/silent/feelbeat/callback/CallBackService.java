package com.silent.feelbeat.callback;

import android.database.Cursor;

/**
 * Created by silent on 7/23/2017.
 */

public interface CallBackService {

    void playMusic(int position, Cursor cursor);
}
