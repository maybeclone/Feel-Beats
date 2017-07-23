package com.silent.feelbeat.musicplayer;

import java.io.IOException;

/**
 * Created by silent on 7/23/2017.
 */

public interface IPlayMusic {

    int PLAY = 1;
    int PAUSE = 2;
    int START = 3;
    int NEXT = 4;
    int PREVIOUS = 5;
    int PLAY_NEW = 6;

    void play(int position);
    int next();
    int previous();
    void pause();
    void start();
    void playNew(int position);
}
