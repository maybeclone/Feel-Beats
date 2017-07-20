package com.silent.feelbeat.dataloaders;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

import com.silent.feelbeat.abstraction.Item;
import com.silent.feelbeat.abstraction.LoaderDB;
import com.silent.feelbeat.models.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by silent on 7/2/2017.
 */

public class SongsLoader extends LoaderDB {

    public final static String PROJECTION[] = {MediaStore.Audio.Media._ID,
                                               MediaStore.Audio.Media.TITLE,
                                               MediaStore.Audio.Media.ARTIST,
                                               MediaStore.Audio.Media.COMPOSER,
                                               MediaStore.Audio.Media.DURATION,
                                               MediaStore.Audio.Media.ALBUM,
                                               MediaStore.Audio.Media.IS_MUSIC,
                                               MediaStore.Audio.Media.ALBUM_ID};

    public final static Uri SONG_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    public final static String ORDER_BY_NAME = PROJECTION[1]+" COLLATE LOCALIZED ASC";

    public SongsLoader(){

    }

    public SongsLoader(ContentResolver contentResolver) {
        super(contentResolver);
    }


    @Override
    public Cursor getCursor() {
        if(contentResolver == null){
            return null;
        }
        return contentResolver.query(SONG_URI, PROJECTION, PROJECTION[6] + "= ?", new String[]{"1"}, ORDER_BY_NAME);
    }

    @Override
    public List<Item> getList() {
        if(contentResolver == null){
            return null;
        }
        Cursor cursor = contentResolver.query(SONG_URI, PROJECTION, null, null, ORDER_BY_NAME);
        if(cursor==null){
            return null;
        }
        List<Item> listSong = new ArrayList<>();
        while(cursor.moveToNext()){
            Song song = new Song(cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getInt(4),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(5),
                    cursor.getLong(7));
            listSong.add(song);
        }
        cursor.close();
        return listSong;
    }

    @Override
    public CursorLoader getCursorLoader(Context context) {
        return new CursorLoader(context,
                                SONG_URI,
                                PROJECTION,
                                PROJECTION[6] + " = ?",
                                new String[]{"1"},
                                ORDER_BY_NAME);
    }

    public static Uri getSongUri(long id){
        return ContentUris.withAppendedId(SONG_URI, id);
    }
}
