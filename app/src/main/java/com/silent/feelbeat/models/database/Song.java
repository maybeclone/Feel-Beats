package com.silent.feelbeat.models.database;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.silent.feelbeat.abstraction.Item;

import java.util.Date;

/**
 * Created by silent on 6/29/2017.
 */

public class Song extends Item implements Parcelable{

    public String album;
    public long albumId;
    public String artist;
    public String artistID;
    public String composer;
    public int duration;

    public Date dateCreated;
    public float rating;
    public String link;
    public String linkImage;
    public String musicKind;

    public Song(){
        super();
        artist = "";
        artist = "";
        composer = "";
        duration = -1;
        albumId = -1;
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
        this.albumId = albumId;
    }

    public Song(long id, String title, String album, long albumId, String artist, String artistID, String composer, int duration) {
        super(id, title);
        this.album = album;
        this.albumId = albumId;
        this.artist = artist;
        this.artistID = artistID;
        this.composer = composer;
        this.duration = duration;
    }

    private Song(Parcel in) {
        super(in.readLong(), in.readString());
        album = in.readString();
        albumId = in.readLong();
        artist = in.readString();
        composer = in.readString();
        duration = in.readInt();
        try{
            link = in.readString();
            linkImage = in.readString();
        } catch (Exception ex){

        }
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
        dest.writeLong(albumId);
        dest.writeString(artist);
        dest.writeString(composer);
        dest.writeInt(duration);
        try {
            dest.writeString(link);
            dest.writeString(linkImage);
        } catch (Exception ex){

        }
    }
}
