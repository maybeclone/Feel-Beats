package com.silent.feelbeat.dataloaders;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

import com.silent.feelbeat.abstraction.Item;
import com.silent.feelbeat.abstraction.LoaderDB;
import com.silent.feelbeat.models.Artist;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by silent on 7/14/2017.
 */

public class ArtistLoader extends LoaderDB {

    public static final String PROJECT[] = {MediaStore.Audio.Artists._ID,
                                            MediaStore.Audio.Artists.ARTIST,
                                            MediaStore.Audio.Artists.ARTIST_KEY,
                                            MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
                                            MediaStore.Audio.Artists.NUMBER_OF_TRACKS};

    public ArtistLoader(){

    }

    public ArtistLoader(ContentResolver contentResolver) {
        super(contentResolver);
    }

    @Override
    public Cursor getCursor() {
        if(contentResolver == null){
            return null;
        }
        return contentResolver.query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, PROJECT, null, null, null);
    }

    @Override
    public List<Item> getList() {
        if(contentResolver == null){
            return null;
        }
        Cursor cursor = contentResolver.query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, PROJECT, null, null, null);
        List<Item> artists = new ArrayList<>();
        while(cursor.moveToNext()){
            artists.add(new Artist(cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getInt(4)));
        }
        cursor.close();
        return artists;
    }

    @Override
    public CursorLoader getCursorLoader(Context context) {
        return new CursorLoader(context,
                    MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, PROJECT,
                    null,
                    null,
                    null);
    }
}
