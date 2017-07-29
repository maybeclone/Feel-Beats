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
    int SEEK_TO = 7;
    int PLAY_NEW_LIST = 8;

    int ON_STOP = 100;
    int ON_RESTART = 101;

    String RECEIVER_INFO = "com.silent.feelbeat.info";
    String RECEVIER_PROCESS = "com.silent.feelbeat.process";

    String EXTRA_PLAYING_POSITION = "position";
    String EXTRA_SONG = "song";

    void play(int position);
    int next();
    int previous();
    void pause();
    void start();
    void seekTo(long position);
    void playNew(int position);
}
