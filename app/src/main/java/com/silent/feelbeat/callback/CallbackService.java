package com.silent.feelbeat.callback;

import com.silent.feelbeat.models.database.Song;

import java.util.ArrayList;

/**
 * Created by silent on 7/23/2017.
 */

public interface CallbackService {

    void playMusic(int position, ArrayList<Song> songs);
}
