package com.silent.feelbeat.models;

import com.silent.feelbeat.abstraction.Item;

/**
 * Created by silent on 6/29/2017.
 */

public class Playlist extends Item{

    public int numOfSongs;

    public Playlist(){
        super();
        numOfSongs = -1;
    }

    public Playlist(long id, String name, int numOfSongs) {
        super(id, name);
        this.numOfSongs = numOfSongs;
    }
}
