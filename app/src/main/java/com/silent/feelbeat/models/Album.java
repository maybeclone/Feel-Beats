package com.silent.feelbeat.models;

import com.silent.feelbeat.abstraction.Item;

/**
 * Created by silent on 6/29/2017.
 */

public class Album extends Item {

    /*
    *   A non human readable key calculated from the ALBUM, used for searching, sorting and grouping
    *   key liked id
    * */
    public String key;
    public String art;
    public int year;
    public int numOfSongs;
    public String artist;

    public Album(){
        super();
        key = "";
        numOfSongs = -1;
        art = "";
    }

    public Album(long id, String title, String key, String art, int numOfSongs){
        super(id, title);
        this.key = key;
        this.numOfSongs = numOfSongs;
        this.art = art;
    }

    public Album(long id, String title, String key, String art, int year, int numOfSongs, String artist) {
        super(id, title);
        this.key = key;
        this.art = art;
        this.year = year;
        this.numOfSongs = numOfSongs;
        this.artist = artist;
    }
}
