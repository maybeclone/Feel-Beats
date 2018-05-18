package com.silent.feelbeat.models;

import com.silent.feelbeat.models.database.Song;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by silent on 5/18/2018.
 */
public class Playlist {

    public int id;
    public String name;
    public ArrayList<Song> songArrayList;
    public Date dateCreated;

}
