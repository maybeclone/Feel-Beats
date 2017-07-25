package com.silent.feelbeat.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import com.silent.feelbeat.abstraction.Item;

/**
 * Created by silent on 6/29/2017.
 */

public class Song extends Item implements Parcelable{

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

    private Song(Parcel in) {
        super(in.readLong(), in.readString());
        album = in.readString();
        ablumId = in.readLong();
        artist = in.readString();
        composer = in.readString();
        duration = in.readInt();
    }

    public int getSeconds(){
        return duration/1000;
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    @Override
    public String toString() {
        return id+" "+title+" "+duration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(album);
        dest.writeLong(ablumId);
        dest.writeString(artist);
        dest.writeString(composer);
        dest.writeInt(duration);
    }
}
