package com.silent.feelbeat.models.database;

import com.silent.feelbeat.abstraction.Item;

/**
 * Created by silent on 6/29/2017.
 */

public class Artist extends Item{


    public String key;
    public int numOfAlbum;
    public int numOfArtist;

    public Artist(){
        super();
        key = "";
        numOfAlbum = -1;
        numOfArtist = -1;
    }

    public Artist(long id, String name, String key, int numOfAlbum, int numOfArtist) {
        super(id, name);
        this.key = key;
        this.numOfAlbum = numOfAlbum;
        this.numOfArtist = numOfArtist;
    }

}
