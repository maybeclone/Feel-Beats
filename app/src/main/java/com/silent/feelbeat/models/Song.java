package com.silent.feelbeat.models;

import android.provider.MediaStore;

import com.silent.feelbeat.abstraction.Item;

/**
 * Created by silent on 6/29/2017.
 */

public class Song extends Item{

    public String album;
    public long ablumId;
    public String artist;
    public String composer;
    public int duration;

    public Song(){
        super();
        artist = "";
        artist = "";
        composer = "";
        duration = -1;
        ablumId = -1;
    }

    public Song(long id, String title, int duration, String artist, String composer, String album){
        super(id, title);
        this.duration = duration;
        this.artist = artist;
        this.composer = composer;
        this.album = album;
    }

    public Song(long id, String title, int duration, String artist, String composer, String album, long albumId){
        super(id, title);
        this.duration = duration;
        this.artist = artist;
        this.composer = composer;
        this.album = album;
        this.ablumId = albumId;
    }

    @Override
    public String toString() {
        return id+" "+title+" "+duration;
    }
}
