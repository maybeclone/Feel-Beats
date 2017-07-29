package com.silent.feelbeat.callback;

import android.database.Cursor;

/**
 * Created by silent on 7/24/2017.
 */

public interface CallbackControl {
    void pause();
    void start();
    void seekTo(long position);
    void next();
    void previous();
}
